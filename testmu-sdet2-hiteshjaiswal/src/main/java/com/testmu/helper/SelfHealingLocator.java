package com.testmu.helper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelfHealingLocator {

    private static final int MAX_CANDIDATES = 1200;
    private static final int REPORT_CANDIDATES = 25;
    private static final int NORMAL_THRESHOLD = 60;
    private static final int DESTRUCTIVE_THRESHOLD = 90;
    private static final int MIN_SCORE_MARGIN = 8;

    private final PageHolder pageHolder = new PageHolder();
    private static Logger Log = LogManager.getLogger(SelfHealingLocator.class.getName());

    public WebElement healFromXpath(String originalXpath, String action) {
        if (!isEnabled() || originalXpath == null || originalXpath.trim().isEmpty()) {
            return null;
        }
        return heal(originalXpath, action);
    }

    public WebElement healFromElement(WebElement element, String action) {
        if (!isEnabled() || element == null) {
            return null;
        }
        String locator = SeleniumHelper.getCleanLocator(element);
        if (locator == null || locator.equals("unknown element")) {
            return null;
        }
        if (locator.startsWith("By.")) {
            return null;
        }
        return heal(locator, action);
    }

    private WebElement heal(String originalLocator, String action) {
        try {
            WebDriver driver = pageHolder.getDriver();
            Hint hint = Hint.from(originalLocator, action);
            List<Candidate> candidates = takeDomSnapshot(driver);
            for (Candidate candidate : candidates) {
                candidate.score = hint.score(candidate);
            }
            candidates.sort(Comparator.comparingInt((Candidate candidate) -> candidate.score).reversed());

            Candidate best = candidates.isEmpty() ? null : candidates.get(0);
            Candidate second = candidates.size() > 1 ? candidates.get(1) : null;
            int threshold = hint.destructive ? DESTRUCTIVE_THRESHOLD : NORMAL_THRESHOLD;

            if (best == null || best.score < threshold) {
                writeReport(originalLocator, action, "not_healed", "top score below threshold", hint, candidates, null);
                return null;
            }
            if (second != null && best.score - second.score < MIN_SCORE_MARGIN) {
                writeReport(originalLocator, action, "not_healed", "ambiguous candidates", hint, candidates, null);
                return null;
            }

            WebElement healedElement = driver.findElement(By.xpath(best.xpath));
            writeReport(originalLocator, action, "healed", "selected high-confidence candidate", hint, candidates, best);
            Reporter.log("Self-healed locator for action " + action + " from [" + originalLocator + "] to ["
                    + best.xpath + "] score=" + best.score, true);
            Log.info("Self-healed locator score=" + best.score + " xpath=" + best.xpath, 2);
            return healedElement;
        } catch (Exception e) {
            Log.info("Unable to self-heal locator " + originalLocator + " | " + e.getMessage(), 2);
            return null;
        }
    }

    private boolean isEnabled() {
        return Boolean.parseBoolean(System.getProperty("self.heal.locators", "false"));
    }

    @SuppressWarnings("unchecked")
    private List<Candidate> takeDomSnapshot(WebDriver driver) {
        String script =
                "const max = arguments[0];" +
                        "function clean(value) {" +
                        "  return (value || '').replace(/\\s+/g, ' ').trim().substring(0, 160);" +
                        "}" +
                        "function xpath(el) {" +
                        "  if (!el || el.nodeType !== 1) return '';" +
                        "  if (el === document.documentElement) return '/html';" +
                        "  const parent = el.parentElement;" +
                        "  if (!parent) return '/' + el.tagName.toLowerCase();" +
                        "  let index = 1;" +
                        "  for (const sibling of parent.children) {" +
                        "    if (sibling === el) break;" +
                        "    if (sibling.tagName === el.tagName) index++;" +
                        "  }" +
                        "  return xpath(parent) + '/' + el.tagName.toLowerCase() + '[' + index + ']';" +
                        "}" +
                        "function visible(el) {" +
                        "  const style = window.getComputedStyle(el);" +
                        "  const rect = el.getBoundingClientRect();" +
                        "  return style && style.display !== 'none' && style.visibility !== 'hidden' && rect.width > 0 && rect.height > 0;" +
                        "}" +
                        "const selector = 'a,button,input,textarea,select,label,[role],span,div,p,li,h1,h2,h3,h4,h5,h6';" +
                        "return Array.from(document.querySelectorAll(selector))" +
                        "  .filter(visible)" +
                        "  .slice(0, max)" +
                        "  .map(el => {" +
                        "    const rect = el.getBoundingClientRect();" +
                        "    return {" +
                        "      tag: clean(el.tagName.toLowerCase())," +
                        "      text: clean(el.innerText || el.textContent)," +
                        "      id: clean(el.id)," +
                        "      name: clean(el.getAttribute('name'))," +
                        "      placeholder: clean(el.getAttribute('placeholder'))," +
                        "      title: clean(el.getAttribute('title'))," +
                        "      ariaLabel: clean(el.getAttribute('aria-label'))," +
                        "      role: clean(el.getAttribute('role'))," +
                        "      className: clean(el.className && el.className.toString ? el.className.toString() : '')," +
                        "      type: clean(el.getAttribute('type'))," +
                        "      value: clean(el.value)," +
                        "      xpath: xpath(el)," +
                        "      x: Math.round(rect.x)," +
                        "      y: Math.round(rect.y)," +
                        "      width: Math.round(rect.width)," +
                        "      height: Math.round(rect.height)," +
                        "      html: clean(el.outerHTML)" +
                        "    };" +
                        "  });";

        Object result = ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(script, MAX_CANDIDATES);
        List<Candidate> candidates = new ArrayList<>();
        if (!(result instanceof List)) {
            return candidates;
        }
        for (Object item : (List<Object>) result) {
            if (item instanceof Map) {
                candidates.add(Candidate.from((Map<String, Object>) item));
            }
        }
        return candidates;
    }

    private void writeReport(String originalLocator, String action, String status, String reason,
                             Hint hint, List<Candidate> candidates, Candidate selected) {
        try {
            Path reportDir = Paths.get(System.getProperty("user.dir"), "target", "self-healing");
            Files.createDirectories(reportDir);
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
            String safeAction = action == null ? "action" : action.replaceAll("[^a-zA-Z0-9_-]", "_");
            Path reportPath = reportDir.resolve(timestamp + "_" + safeAction + ".json");

            WebDriver driver = pageHolder.getDriver();
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            appendJson(json, "status", status, 1, true);
            appendJson(json, "reason", reason, 1, true);
            appendJson(json, "action", action, 1, true);
            appendJson(json, "url", safeDriverValue(driver, "url"), 1, true);
            appendJson(json, "title", safeDriverValue(driver, "title"), 1, true);
            appendJson(json, "originalLocator", originalLocator, 1, true);
            appendJson(json, "expectedTag", hint.expectedTag, 1, true);
            appendJson(json, "destructive", String.valueOf(hint.destructive), 1, true);
            json.append("  \"hints\": ").append(toJsonArray(hint.tokens)).append(",\n");
            json.append("  \"selected\": ");
            appendCandidate(json, selected, 1);
            json.append(",\n");
            json.append("  \"candidates\": [\n");
            int limit = Math.min(REPORT_CANDIDATES, candidates.size());
            for (int i = 0; i < limit; i++) {
                appendCandidate(json, candidates.get(i), 2);
                if (i < limit - 1) {
                    json.append(",");
                }
                json.append("\n");
            }
            json.append("  ]\n");
            json.append("}\n");

            Files.write(reportPath, json.toString().getBytes(StandardCharsets.UTF_8));
            Reporter.log("Self-healing locator report: " + reportPath, true);
        } catch (Exception e) {
            Log.info("Unable to write self-healing report: " + e.getMessage(), 2);
        }
    }

    private String safeDriverValue(WebDriver driver, String field) {
        try {
            if ("url".equals(field)) {
                return driver.getCurrentUrl();
            }
            return driver.getTitle();
        } catch (Exception e) {
            return "";
        }
    }

    private void appendJson(StringBuilder json, String key, String value, int indent, boolean comma) {
        json.append(spaces(indent)).append("\"").append(escape(key)).append("\": \"")
                .append(escape(value)).append("\"");
        if (comma) {
            json.append(",");
        }
        json.append("\n");
    }

    private void appendCandidate(StringBuilder json, Candidate candidate, int indent) {
        if (candidate == null) {
            json.append("null");
            return;
        }
        json.append("{\n");
        appendJson(json, "score", String.valueOf(candidate.score), indent + 1, true);
        appendJson(json, "tag", candidate.tag, indent + 1, true);
        appendJson(json, "text", candidate.text, indent + 1, true);
        appendJson(json, "id", candidate.id, indent + 1, true);
        appendJson(json, "name", candidate.name, indent + 1, true);
        appendJson(json, "placeholder", candidate.placeholder, indent + 1, true);
        appendJson(json, "title", candidate.title, indent + 1, true);
        appendJson(json, "ariaLabel", candidate.ariaLabel, indent + 1, true);
        appendJson(json, "role", candidate.role, indent + 1, true);
        appendJson(json, "className", candidate.className, indent + 1, true);
        appendJson(json, "xpath", candidate.xpath, indent + 1, true);
        appendJson(json, "html", candidate.html, indent + 1, false);
        json.append(spaces(indent)).append("}");
    }

    private String toJsonArray(List<String> values) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < values.size(); i++) {
            json.append("\"").append(escape(values.get(i))).append("\"");
            if (i < values.size() - 1) {
                json.append(", ");
            }
        }
        json.append("]");
        return json.toString();
    }

    private String spaces(int indent) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            builder.append("  ");
        }
        return builder.toString();
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static class Hint {
        private final String originalLocator;
        private final String action;
        private final String expectedTag;
        private final boolean destructive;
        private final List<String> tokens;
        private final Map<String, String> attributes;
        private final Set<String> classTokens;

        private Hint(String originalLocator, String action, String expectedTag, boolean destructive,
                     List<String> tokens, Map<String, String> attributes, Set<String> classTokens) {
            this.originalLocator = originalLocator == null ? "" : originalLocator;
            this.action = action == null ? "" : action;
            this.expectedTag = expectedTag;
            this.destructive = destructive;
            this.tokens = tokens;
            this.attributes = attributes;
            this.classTokens = classTokens;
        }

        static Hint from(String originalLocator, String action) {
            String locator = originalLocator == null ? "" : originalLocator;
            String expectedTag = firstMatch(locator, "\\(?//([a-zA-Z][a-zA-Z0-9_-]*)");

            LinkedHashSet<String> tokenSet = new LinkedHashSet<>();
            Matcher literalMatcher = Pattern.compile("['\"]([^'\"]{2,120})['\"]").matcher(locator);
            while (literalMatcher.find()) {
                tokenSet.add(literalMatcher.group(1).trim());
            }

            Map<String, String> attrs = new LinkedHashMap<>();
            collectAttribute(locator, attrs, tokenSet, "id");
            collectAttribute(locator, attrs, tokenSet, "name");
            collectAttribute(locator, attrs, tokenSet, "placeholder");
            collectAttribute(locator, attrs, tokenSet, "title");
            collectAttribute(locator, attrs, tokenSet, "aria-label");
            collectAttribute(locator, attrs, tokenSet, "role");

            Set<String> classTokens = new LinkedHashSet<>();
            Matcher classMatcher = Pattern.compile("contains\\(@class,\\s*['\"]([^'\"]+)['\"]\\)").matcher(locator);
            while (classMatcher.find()) {
                classTokens.add(classMatcher.group(1).trim());
            }

            String safetyText = (locator + " " + action + " " + tokenSet).toLowerCase(Locale.ROOT);
            boolean destructive = safetyText.contains("delete")
                    || safetyText.contains("remove")
                    || safetyText.contains("revoke")
                    || safetyText.contains("rollback")
                    || safetyText.contains("confirm");

            return new Hint(locator, action, expectedTag, destructive, new ArrayList<>(tokenSet), attrs, classTokens);
        }

        private static void collectAttribute(String locator, Map<String, String> attrs,
                                             Set<String> tokens, String attribute) {
            String regex = "@" + Pattern.quote(attribute) + "\\s*=\\s*['\"]([^'\"]+)['\"]";
            Matcher matcher = Pattern.compile(regex).matcher(locator);
            if (matcher.find()) {
                String value = matcher.group(1).trim();
                attrs.put(attribute, value);
                tokens.add(value);
            }
        }

        private static String firstMatch(String value, String regex) {
            Matcher matcher = Pattern.compile(regex).matcher(value == null ? "" : value);
            if (matcher.find()) {
                return matcher.group(1).toLowerCase(Locale.ROOT);
            }
            return "";
        }

        int score(Candidate candidate) {
            int score = 0;
            if (!expectedTag.isEmpty() && expectedTag.equalsIgnoreCase(candidate.tag)) {
                score += 10;
            }

            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                String actual = candidate.attributeValue(entry.getKey());
                score += scoreExactOrContains(actual, entry.getValue(), 65, 30);
            }

            for (String token : tokens) {
                score += scoreExactOrContains(candidate.text, token, 50, 30);
                score += scoreExactOrContains(candidate.value, token, 45, 25);
                score += scoreExactOrContains(candidate.id, token, 40, 20);
                score += scoreExactOrContains(candidate.name, token, 45, 25);
                score += scoreExactOrContains(candidate.placeholder, token, 45, 25);
                score += scoreExactOrContains(candidate.title, token, 45, 25);
                score += scoreExactOrContains(candidate.ariaLabel, token, 45, 25);
            }

            for (String classToken : classTokens) {
                if (containsIgnoreCase(candidate.className, classToken)) {
                    score += 15;
                }
            }

            if ("click".equalsIgnoreCase(action) && candidate.looksClickable()) {
                score += 8;
            }
            return score;
        }

        private int scoreExactOrContains(String actual, String expected, int exact, int contains) {
            if (actual == null || expected == null || expected.trim().isEmpty()) {
                return 0;
            }
            String normalizedActual = actual.trim().toLowerCase(Locale.ROOT);
            String normalizedExpected = expected.trim().toLowerCase(Locale.ROOT);
            if (normalizedActual.equals(normalizedExpected)) {
                return exact;
            }
            if (normalizedActual.contains(normalizedExpected) || normalizedExpected.contains(normalizedActual)) {
                return contains;
            }
            return 0;
        }

        private boolean containsIgnoreCase(String actual, String expected) {
            return actual != null && expected != null
                    && actual.toLowerCase(Locale.ROOT).contains(expected.toLowerCase(Locale.ROOT));
        }
    }

    private static class Candidate {
        private int score;
        private String tag;
        private String text;
        private String id;
        private String name;
        private String placeholder;
        private String title;
        private String ariaLabel;
        private String role;
        private String className;
        private String type;
        private String value;
        private String xpath;
        private String html;

        static Candidate from(Map<String, Object> values) {
            Candidate candidate = new Candidate();
            candidate.tag = stringValue(values.get("tag"));
            candidate.text = stringValue(values.get("text"));
            candidate.id = stringValue(values.get("id"));
            candidate.name = stringValue(values.get("name"));
            candidate.placeholder = stringValue(values.get("placeholder"));
            candidate.title = stringValue(values.get("title"));
            candidate.ariaLabel = stringValue(values.get("ariaLabel"));
            candidate.role = stringValue(values.get("role"));
            candidate.className = stringValue(values.get("className"));
            candidate.type = stringValue(values.get("type"));
            candidate.value = stringValue(values.get("value"));
            candidate.xpath = stringValue(values.get("xpath"));
            candidate.html = stringValue(values.get("html"));
            return candidate;
        }

        private static String stringValue(Object value) {
            return value == null ? "" : String.valueOf(value);
        }

        String attributeValue(String attribute) {
            if ("id".equals(attribute)) {
                return id;
            }
            if ("name".equals(attribute)) {
                return name;
            }
            if ("placeholder".equals(attribute)) {
                return placeholder;
            }
            if ("title".equals(attribute)) {
                return title;
            }
            if ("aria-label".equals(attribute)) {
                return ariaLabel;
            }
            if ("role".equals(attribute)) {
                return role;
            }
            return "";
        }

        boolean looksClickable() {
            return "button".equalsIgnoreCase(tag)
                    || "a".equalsIgnoreCase(tag)
                    || "button".equalsIgnoreCase(role)
                    || "menuitem".equalsIgnoreCase(role)
                    || className.toLowerCase(Locale.ROOT).contains("btn")
                    || className.toLowerCase(Locale.ROOT).contains("button")
                    || "checkbox".equalsIgnoreCase(type)
                    || "radio".equalsIgnoreCase(type);
        }
    }
}
