/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package commons.validator.routines;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;

/**
 * <p><b>Domain name</b> validation routines.</p>
 *
 * <p>
 * This validator provides methods for validating Internet domain names
 * and top-level domains.
 * </p>
 *
 * <p>Domain names are evaluated according
 * to the standards <a href="http://www.ietf.org/rfc/rfc1034.txt">RFC1034</a>,
 * section 3, and <a href="http://www.ietf.org/rfc/rfc1123.txt">RFC1123</a>,
 * section 2.1. No accommodation is provided for the specialized needs of
 * other applications; if the domain name has been URL-encoded, for example,
 * validation will fail even though the equivalent plaintext version of the
 * same name would have passed.
 * </p>
 *
 * <p>
 * Validation is also provided for top-level domains (TLDs) as defined and
 * maintained by the Internet Assigned Numbers Authority (IANA):
 * </p>
 *
 *   <ul>
 *     <li>{@link #isValidInfrastructureTld} - validates infrastructure TLDs
 *         (<code>.arpa</code>, etc.)</li>
 *     <li>{@link #isValidGenericTld} - validates generic TLDs
 *         (<code>.com, .org</code>, etc.)</li>
 *     <li>{@link #isValidCountryCodeTld} - validates country code TLDs
 *         (<code>.us, .uk, .cn</code>, etc.)</li>
 *   </ul>
 *
 * <p>
 * (<b>NOTE</b>: This class does not provide IP address lookup for domain names or
 * methods to ensure that a given domain name matches a specific IP; see
 * {@link java.net.InetAddress} for that functionality.)
 * </p>
 *
 * @version $Revision$
 * @since Validator 1.4
 */
public class DomainValidator implements Serializable {

    private static final long serialVersionUID = -4407125112880174009L;

    // Regular expression strings for hostnames (derived from RFC2396 and RFC 1123)

    // RFC2396: domainlabel   = alphanum | alphanum *( alphanum | "-" ) alphanum
    // Max 63 characters
    private static final String DOMAIN_LABEL_REGEX = "\\p{Alnum}(?>[\\p{Alnum}-]{0,61}\\p{Alnum})?";

    // RFC2396 toplabel = alpha | alpha *( alphanum | "-" ) alphanum
    // Max 63 characters
    private static final String TOP_LABEL_REGEX = "\\p{Alpha}(?>[\\p{Alnum}-]{0,61}\\p{Alnum})?";

    // RFC2396 hostname = *( domainlabel "." ) toplabel [ "." ]
    // Note that the regex currently requires both a domain label and a top level label, whereas
    // the RFC does not. This is because the regex is used to detect if a TLD is present.
    // If the match fails, input is checked against DOMAIN_LABEL_REGEX (hostnameRegex)
    // RFC1123 sec 2.1 allows hostnames to start with a digit
    private static final String DOMAIN_NAME_REGEX =
            "^(?:" + DOMAIN_LABEL_REGEX + "\\.)+" + "(" + TOP_LABEL_REGEX + ")\\.?$";

    private final boolean allowLocal;

    /**
     * Singleton instance of this validator, which
     *  doesn't consider local addresses as valid.
     */
    private static final DomainValidator DOMAIN_VALIDATOR = new DomainValidator(false);

    /**
     * Singleton instance of this validator, which does
     *  consider local addresses valid.
     */
    private static final DomainValidator DOMAIN_VALIDATOR_WITH_LOCAL = new DomainValidator(true);

    /**
     * RegexValidator for matching domains.
     */
    private final RegexValidator domainRegex =
            new RegexValidator(DOMAIN_NAME_REGEX);
    /**
     * RegexValidator for matching a local hostname
     */
    // RFC1123 sec 2.1 allows hostnames to start with a digit
    private final RegexValidator hostnameRegex =
            new RegexValidator(DOMAIN_LABEL_REGEX);

    /**
     * Returns the singleton instance of this validator. It
     *  will not consider local addresses as valid.
     * @return the singleton instance of this validator
     */
    public static DomainValidator getInstance() {
        return DOMAIN_VALIDATOR;
    }

    /**
     * Returns the singleton instance of this validator,
     *  with local validation as required.
     * @param allowLocal Should local addresses be considered valid?
     * @return the singleton instance of this validator
     */
    public static DomainValidator getInstance(boolean allowLocal) {
       if(allowLocal) {
          return DOMAIN_VALIDATOR_WITH_LOCAL;
       }
       return DOMAIN_VALIDATOR;
    }

    /** Private constructor. */
    private DomainValidator(boolean allowLocal) {
       this.allowLocal = allowLocal;
    }

    /**
     * Returns true if the specified <code>String</code> parses
     * as a valid domain name with a recognized top-level domain.
     * The parsing is case-insensitive.
     * @param domain the parameter to check for domain name syntax
     * @return true if the parameter is a valid domain name
     */
    public boolean isValid(String domain) {
        if (domain == null) {
            return false;
        }
        domain = unicodeToASCII(domain);
        // hosts must be equally reachable via punycode and Unicode;
        // Unicode is never shorter than punycode, so check punycode
        // if domain did not convert, then it will be caught by ASCII
        // checks in the regexes below
        if (domain.length() > 253) {
            return false;
        }
        String[] groups = domainRegex.match(domain);
        if (groups != null && groups.length > 0) {
            return isValidTld(groups[0]);
        }
        return allowLocal && hostnameRegex.isValid(domain);
    }

    // package protected for unit test access
    // must agree with isValid() above
    final boolean isValidDomainSyntax(String domain) {
        if (domain == null) {
            return false;
        }
        domain = unicodeToASCII(domain);
        // hosts must be equally reachable via punycode and Unicode;
        // Unicode is never shorter than punycode, so check punycode
        // if domain did not convert, then it will be caught by ASCII
        // checks in the regexes below
        if (domain.length() > 253) {
            return false;
        }
        String[] groups = domainRegex.match(domain);
        return (groups != null && groups.length > 0)
                || hostnameRegex.isValid(domain);
    }

    /**
     * Returns true if the specified <code>String</code> matches any
     * IANA-defined top-level domain. Leading dots are ignored if present.
     * The search is case-insensitive.
     * @param tld the parameter to check for TLD status, not null
     * @return true if the parameter is a TLD
     */
    public boolean isValidTld(String tld) {
        tld = unicodeToASCII(tld);
        if(allowLocal && isValidLocalTld(tld)) {
           return true;
        }
        return isValidInfrastructureTld(tld)
                || isValidGenericTld(tld)
                || isValidCountryCodeTld(tld);
    }

    /**
     * Returns true if the specified <code>String</code> matches any
     * IANA-defined infrastructure top-level domain. Leading dots are
     * ignored if present. The search is case-insensitive.
     * @param iTld the parameter to check for infrastructure TLD status, not null
     * @return true if the parameter is an infrastructure TLD
     */
    public boolean isValidInfrastructureTld(String iTld) {
        iTld = unicodeToASCII(iTld);
        return Arrays.binarySearch(INFRASTRUCTURE_TLDS, (chompLeadingDot(iTld.toLowerCase(Locale.ENGLISH)))) >= 0;
    }

    /**
     * Returns true if the specified <code>String</code> matches any
     * IANA-defined generic top-level domain. Leading dots are ignored
     * if present. The search is case-insensitive.
     * @param gTld the parameter to check for generic TLD status, not null
     * @return true if the parameter is a generic TLD
     */
    public boolean isValidGenericTld(String gTld) {
        gTld = unicodeToASCII(gTld);
        return Arrays.binarySearch(GENERIC_TLDS, chompLeadingDot(gTld.toLowerCase(Locale.ENGLISH))) >= 0;
    }

    /**
     * Returns true if the specified <code>String</code> matches any
     * IANA-defined country code top-level domain. Leading dots are
     * ignored if present. The search is case-insensitive.
     * @param ccTld the parameter to check for country code TLD status, not null
     * @return true if the parameter is a country code TLD
     */
    public boolean isValidCountryCodeTld(String ccTld) {
        ccTld = unicodeToASCII(ccTld);
        return Arrays.binarySearch(COUNTRY_CODE_TLDS, chompLeadingDot(ccTld.toLowerCase(Locale.ENGLISH))) >= 0;
    }

    /**
     * Returns true if the specified <code>String</code> matches any
     * widely used "local" domains (localhost or localdomain). Leading dots are
     * ignored if present. The search is case-insensitive.
     * @param lTld the parameter to check for local TLD status, not null
     * @return true if the parameter is an local TLD
     */
    public boolean isValidLocalTld(String lTld) {
        lTld = unicodeToASCII(lTld);
        return Arrays.binarySearch(LOCAL_TLDS, chompLeadingDot(lTld.toLowerCase(Locale.ENGLISH))) >= 0;
    }

    private String chompLeadingDot(String str) {
        if (str.startsWith(".")) {
            return str.substring(1);
        }
        return str;
    }

    // ---------------------------------------------
    // ----- TLDs defined by IANA
    // ----- Authoritative and comprehensive list at:
    // ----- http://data.iana.org/TLD/tlds-alpha-by-domain.txt

    // Note that the above list is in UPPER case.
    // The code currently converts strings to lower case (as per the tables below)

    // IANA also provide an HTML list at http://www.iana.org/domains/root/db
    // Note that this contains several country code entries which are NOT in
    // the text file. These all have the "Not assigned" in the "Sponsoring Organisation" column
    // For example (as of 2015-01-02):
    // .bl  country-code    Not assigned
    // .um  country-code    Not assigned

    // WARNING: this array MUST be sorted, others it cannot be searched reliably using binary search
    private static final String[] INFRASTRUCTURE_TLDS = new String[] {
        "arpa",               // internet infrastructure
    };

    // WARNING: this array MUST be sorted, others it cannot be searched reliably using binary search
    private static final String[] GENERIC_TLDS = new String[] {
        "abogado",
        "academy",
        "accountants",
        "active",
        "actor",
        "adult",
        "aero",
        "agency",
        "airforce",
        "allfinanz",
        "alsace",
        "amsterdam",
        "android",
        "aquarelle",
        "archi",
        "army",
        "arpa",
        "asia",
        "associates",
        "attorney",
        "auction",
        "audio",
        "autos",
        "axa",
        "band",
        "bar",
        "bargains",
        "bayern",
        "beer",
        "berlin",
        "best",
        "bid",
        "bike",
        "bio",
        "biz",
        "black",
        "blackfriday",
        "bloomberg",
        "blue",
        "bmw",
        "bnpparibas",
        "boo",
        "boutique",
        "brussels",
        "budapest",
        "build",
        "builders",
        "business",
        "buzz",
        "bzh",
        "cab",
        "cal",
        "camera",
        "camp",
        "cancerresearch",
        "capetown",
        "capital",
        "caravan",
        "cards",
        "care",
        "career",
        "careers",
        "cartier",
        "casa",
        "cash",
        "cat",
        "catering",
        "center",
        "ceo",
        "cern",
        "channel",
        "cheap",
        "christmas",
        "chrome",
        "church",
        "citic",
        "city",
        "claims",
        "cleaning",
        "click",
        "clinic",
        "clothing",
        "club",
        "coach",
        "codes",
        "coffee",
        "college",
        "cologne",
        "com",
        "community",
        "company",
        "computer",
        "condos",
        "construction",
        "consulting",
        "contractors",
        "cooking",
        "cool",
        "coop",
        "country",
        "credit",
        "creditcard",
        "cricket",
        "crs",
        "cruises",
        "cuisinella",
        "cymru",
        "dad",
        "dance",
        "dating",
        "day",
        "deals",
        "degree",
        "delivery",
        "democrat",
        "dental",
        "dentist",
        "desi",
        "dev",
        "diamonds",
        "diet",
        "digital",
        "direct",
        "directory",
        "discount",
        "dnp",
        "docs",
        "domains",
        "doosan",
        "durban",
        "dvag",
        "eat",
        "edu",
        "education",
        "email",
        "emerck",
        "energy",
        "engineer",
        "engineering",
        "enterprises",
        "equipment",
        "esq",
        "estate",
        "eurovision",
        "eus",
        "events",
        "everbank",
        "exchange",
        "expert",
        "exposed",
        "fail",
        "farm",
        "fashion",
        "feedback",
        "finance",
        "financial",
        "firmdale",
        "fish",
        "fishing",
        "fitness",
        "flights",
        "florist",
        "flowers",
        "flsmidth",
        "fly",
        "foo",
        "forsale",
        "foundation",
        "frl",
        "frogans",
        "fund",
        "furniture",
        "futbol",
        "gal",
        "gallery",
        "garden",
        "gbiz",
        "gent",
        "ggee",
        "gift",
        "gifts",
        "gives",
        "glass",
        "gle",
        "global",
        "globo",
        "gmail",
        "gmo",
        "gmx",
        "google",
        "gop",
        "gov",
        "graphics",
        "gratis",
        "green",
        "gripe",
        "guide",
        "guitars",
        "guru",
        "hamburg",
        "haus",
        "healthcare",
        "help",
        "here",
        "hiphop",
        "hiv",
        "holdings",
        "holiday",
        "homes",
        "horse",
        "host",
        "hosting",
        "house",
        "how",
        "ibm",
        "immo",
        "immobilien",
        "industries",
        "info",
        "ing",
        "ink",
        "institute",
        "insure",
        "int",
        "international",
        "investments",
        "irish",
        "iwc",
        "jetzt",
        "jobs",
        "joburg",
        "juegos",
        "kaufen",
        "kim",
        "kitchen",
        "kiwi",
        "koeln",
        "krd",
        "kred",
        "lacaixa",
        "land",
        "latrobe",
        "lawyer",
        "lds",
        "lease",
        "legal",
        "lgbt",
        "lidl",
        "life",
        "lighting",
        "limited",
        "limo",
        "link",
        "loans",
        "london",
        "lotto",
        "ltda",
        "luxe",
        "luxury",
        "madrid",
        "maison",
        "management",
        "mango",
        "market",
        "marketing",
        "media",
        "meet",
        "melbourne",
        "meme",
        "memorial",
        "menu",
        "miami",
        "mil",
        "mini",
        "mobi",
        "moda",
        "moe",
        "monash",
        "money",
        "mormon",
        "mortgage",
        "moscow",
        "motorcycles",
        "mov",
        "museum",
        "nagoya",
        "name",
        "navy",
        "net",
        "network",
        "neustar",
        "new",
        "nexus",
        "ngo",
        "nhk",
        "ninja",
        "nra",
        "nrw",
        "nyc",
        "okinawa",
        "ong",
        "onl",
        "ooo",
        "org",
        "organic",
        "osaka",
        "otsuka",
        "ovh",
        "paris",
        "partners",
        "parts",
        "party",
        "pharmacy",
        "photo",
        "photography",
        "photos",
        "physio",
        "pics",
        "pictures",
        "pink",
        "pizza",
        "place",
        "plumbing",
        "pohl",
        "poker",
        "porn",
        "post",
        "praxi",
        "press",
        "pro",
        "prod",
        "productions",
        "prof",
        "properties",
        "property",
        "pub",
        "qpon",
        "quebec",
        "realtor",
        "recipes",
        "red",
        "rehab",
        "reise",
        "reisen",
        "reit",
        "ren",
        "rentals",
        "repair",
        "report",
        "republican",
        "rest",
        "restaurant",
        "reviews",
        "rich",
        "rio",
        "rip",
        "rocks",
        "rodeo",
        "rsvp",
        "ruhr",
        "ryukyu",
        "saarland",
        "sale",
        "samsung",
        "sarl",
        "sca",
        "scb",
        "schmidt",
        "schule",
        "schwarz",
        "science",
        "scot",
        "services",
        "sew",
        "sexy",
        "shiksha",
        "shoes",
        "shriram",
        "singles",
        "sky",
        "social",
        "software",
        "sohu",
        "solar",
        "solutions",
        "soy",
        "space",
        "spiegel",
        "supplies",
        "supply",
        "support",
        "surf",
        "surgery",
        "suzuki",
        "sydney",
        "systems",
        "taipei",
        "tatar",
        "tattoo",
        "tax",
        "technology",
        "tel",
        "tienda",
        "tips",
        "tires",
        "tirol",
        "today",
        "tokyo",
        "tools",
        "top",
        "town",
        "toys",
        "trade",
        "training",
        "travel",
        "trust",
        "tui",
        "university",
        "uno",
        "uol",
        "vacations",
        "vegas",
        "ventures",
        "versicherung",
        "vet",
        "viajes",
        "video",
        "villas",
        "vision",
        "vlaanderen",
        "vodka",
        "vote",
        "voting",
        "voto",
        "voyage",
        "wales",
        "wang",
        "watch",
        "webcam",
        "website",
        "wed",
        "wedding",
        "whoswho",
        "wien",
        "wiki",
        "williamhill",
        "wme",
        "work",
        "works",
        "world",
        "wtc",
        "wtf",
        "xn--1qqw23a", // 佛山 Guangzhou YU Wei Information Technology Co., Ltd.
        "xn--3bst00m", // 集团 Eagle Horizon Limited
        "xn--3ds443g", // 在线 TLD REGISTRY LIMITED
        "xn--45q11c", // 八卦 Zodiac Scorpio Limited
        "xn--4gbrim", // موقع Suhub Electronic Establishment
        "xn--55qw42g", // 公益 China Organizational Name Administration Center
        "xn--55qx5d", // 公司 Computer Network Information Center of Chinese Academy of Sciences （China Internet Network Information Center）
        "xn--6frz82g", // 移动 Afilias Limited
        "xn--6qq986b3xl", // 我爱你 Tycoon Treasure Limited
        "xn--80adxhks", // москва Foundation for Assistance for Internet Technologies and Infrastructure Development (FAITID)
        "xn--80asehdb", // онлайн CORE Association
        "xn--80aswg", // сайт CORE Association
        "xn--c1avg", // орг Public Interest Registry
        "xn--cg4bki", // 삼성 SAMSUNG SDS CO., LTD
        "xn--czr694b", // 商标 HU YI GLOBAL INFORMATION RESOURCES(HOLDING) COMPANY.HONGKONG LIMITED
        "xn--czrs0t", // 商店 Wild Island, LLC
        "xn--czru2d", // 商城 Zodiac Aquarius Limited
        "xn--d1acj3b", // дети The Foundation for Network Initiatives “The Smart Internet”
        "xn--fiq228c5hs", // 中文网 TLD REGISTRY LIMITED
        "xn--fiq64b", // 中信 CITIC Group Corporation
        "xn--flw351e", // 谷歌 Charleston Road Registry Inc.
        "xn--hxt814e", // 网店 Zodiac Libra Limited
        "xn--i1b6b1a6a2e", // संगठन Public Interest Registry
        "xn--io0a7i", // 网络 Computer Network Information Center of Chinese Academy of Sciences （China Internet Network Information Center）
        "xn--kput3i", // 手机 Beijing RITT-Net Technology Development Co., Ltd
        "xn--mgbab2bd", // بازار CORE Association
        "xn--ngbc5azd", // شبكة International Domain Registry Pty. Ltd.
        "xn--nqv7f", // 机构 Public Interest Registry
        "xn--nqv7fs00ema", // 组织机构 Public Interest Registry
        "xn--p1acf", // рус Rusnames Limited
        "xn--q9jyb4c", // みんな Charleston Road Registry Inc.
        "xn--qcka1pmc", // グーグル Charleston Road Registry Inc.
        "xn--rhqv96g", // 世界 Stable Tone Limited
        "xn--ses554g", // 网址 HU YI GLOBAL INFORMATION RESOURCES (HOLDING) COMPANY. HONGKONG LIMITED
        "xn--unup4y", // 游戏 Spring Fields, LLC
        "xn--vermgensberater-ctb", // vermögensberater Deutsche Vermögensberatung Aktiengesellschaft DVAG
        "xn--vermgensberatung-pwb", // vermögensberatung Deutsche Vermögensberatung Aktiengesellschaft DVAG
        "xn--vhquv", // 企业 Dash McCook, LLC
        "xn--xhq521b", // 广东 Guangzhou YU Wei Information Technology Co., Ltd.
        "xn--zfr164b", // 政务 China Organizational Name Administration Center
        "xxx",
        "xyz",
        "yachts",
        "yandex",
        "yoga",
        "yokohama",
        "youtube",
        "zip",
        "zone",
        "zuerich",
   };

    // WARNING: this array MUST be sorted, others it cannot be searched reliably using binary search
    private static final String[] COUNTRY_CODE_TLDS = new String[] {
        "ac",                 // Ascension Island
        "ad",                 // Andorra
        "ae",                 // United Arab Emirates
        "af",                 // Afghanistan
        "ag",                 // Antigua and Barbuda
        "ai",                 // Anguilla
        "al",                 // Albania
        "am",                 // Armenia
        "an",                 // Netherlands Antilles
        "ao",                 // Angola
        "aq",                 // Antarctica
        "ar",                 // Argentina
        "as",                 // American Samoa
        "at",                 // Austria
        "au",                 // Australia (includes Ashmore and Cartier Islands and Coral Sea Islands)
        "aw",                 // Aruba
        "ax",                 // Åland
        "az",                 // Azerbaijan
        "ba",                 // Bosnia and Herzegovina
        "bb",                 // Barbados
        "bd",                 // Bangladesh
        "be",                 // Belgium
        "bf",                 // Burkina Faso
        "bg",                 // Bulgaria
        "bh",                 // Bahrain
        "bi",                 // Burundi
        "bj",                 // Benin
        "bm",                 // Bermuda
        "bn",                 // Brunei Darussalam
        "bo",                 // Bolivia
        "br",                 // Brazil
        "bs",                 // Bahamas
        "bt",                 // Bhutan
        "bv",                 // Bouvet Island
        "bw",                 // Botswana
        "by",                 // Belarus
        "bz",                 // Belize
        "ca",                 // Canada
        "cc",                 // Cocos (Keeling) Islands
        "cd",                 // Democratic Republic of the Congo (formerly Zaire)
        "cf",                 // Central African Republic
        "cg",                 // Republic of the Congo
        "ch",                 // Switzerland
        "ci",                 // Côte d'Ivoire
        "ck",                 // Cook Islands
        "cl",                 // Chile
        "cm",                 // Cameroon
        "cn",                 // China, mainland
        "co",                 // Colombia
        "cr",                 // Costa Rica
        "cu",                 // Cuba
        "cv",                 // Cape Verde
        "cw",                 // Curaçao
        "cx",                 // Christmas Island
        "cy",                 // Cyprus
        "cz",                 // Czech Republic
        "de",                 // Germany
        "dj",                 // Djibouti
        "dk",                 // Denmark
        "dm",                 // Dominica
        "do",                 // Dominican Republic
        "dz",                 // Algeria
        "ec",                 // Ecuador
        "ee",                 // Estonia
        "eg",                 // Egypt
        "er",                 // Eritrea
        "es",                 // Spain
        "et",                 // Ethiopia
        "eu",                 // European Union
        "fi",                 // Finland
        "fj",                 // Fiji
        "fk",                 // Falkland Islands
        "fm",                 // Federated States of Micronesia
        "fo",                 // Faroe Islands
        "fr",                 // France
        "ga",                 // Gabon
        "gb",                 // Great Britain (United Kingdom)
        "gd",                 // Grenada
        "ge",                 // Georgia
        "gf",                 // French Guiana
        "gg",                 // Guernsey
        "gh",                 // Ghana
        "gi",                 // Gibraltar
        "gl",                 // Greenland
        "gm",                 // The Gambia
        "gn",                 // Guinea
        "gp",                 // Guadeloupe
        "gq",                 // Equatorial Guinea
        "gr",                 // Greece
        "gs",                 // South Georgia and the South Sandwich Islands
        "gt",                 // Guatemala
        "gu",                 // Guam
        "gw",                 // Guinea-Bissau
        "gy",                 // Guyana
        "hk",                 // Hong Kong
        "hm",                 // Heard Island and McDonald Islands
        "hn",                 // Honduras
        "hr",                 // Croatia (Hrvatska)
        "ht",                 // Haiti
        "hu",                 // Hungary
        "id",                 // Indonesia
        "ie",                 // Ireland (Éire)
        "il",                 // Israel
        "im",                 // Isle of Man
        "in",                 // India
        "io",                 // British Indian Ocean Territory
        "iq",                 // Iraq
        "ir",                 // Iran
        "is",                 // Iceland
        "it",                 // Italy
        "je",                 // Jersey
        "jm",                 // Jamaica
        "jo",                 // Jordan
        "jp",                 // Japan
        "ke",                 // Kenya
        "kg",                 // Kyrgyzstan
        "kh",                 // Cambodia (Khmer)
        "ki",                 // Kiribati
        "km",                 // Comoros
        "kn",                 // Saint Kitts and Nevis
        "kp",                 // North Korea
        "kr",                 // South Korea
        "kw",                 // Kuwait
        "ky",                 // Cayman Islands
        "kz",                 // Kazakhstan
        "la",                 // Laos (currently being marketed as the official domain for Los Angeles)
        "lb",                 // Lebanon
        "lc",                 // Saint Lucia
        "li",                 // Liechtenstein
        "lk",                 // Sri Lanka
        "lr",                 // Liberia
        "ls",                 // Lesotho
        "lt",                 // Lithuania
        "lu",                 // Luxembourg
        "lv",                 // Latvia
        "ly",                 // Libya
        "ma",                 // Morocco
        "mc",                 // Monaco
        "md",                 // Moldova
        "me",                 // Montenegro
        "mg",                 // Madagascar
        "mh",                 // Marshall Islands
        "mk",                 // Republic of Macedonia
        "ml",                 // Mali
        "mm",                 // Myanmar
        "mn",                 // Mongolia
        "mo",                 // Macau
        "mp",                 // Northern Mariana Islands
        "mq",                 // Martinique
        "mr",                 // Mauritania
        "ms",                 // Montserrat
        "mt",                 // Malta
        "mu",                 // Mauritius
        "mv",                 // Maldives
        "mw",                 // Malawi
        "mx",                 // Mexico
        "my",                 // Malaysia
        "mz",                 // Mozambique
        "na",                 // Namibia
        "nc",                 // New Caledonia
        "ne",                 // Niger
        "nf",                 // Norfolk Island
        "ng",                 // Nigeria
        "ni",                 // Nicaragua
        "nl",                 // Netherlands
        "no",                 // Norway
        "np",                 // Nepal
        "nr",                 // Nauru
        "nu",                 // Niue
        "nz",                 // New Zealand
        "om",                 // Oman
        "pa",                 // Panama
        "pe",                 // Peru
        "pf",                 // French Polynesia With Clipperton Island
        "pg",                 // Papua New Guinea
        "ph",                 // Philippines
        "pk",                 // Pakistan
        "pl",                 // Poland
        "pm",                 // Saint-Pierre and Miquelon
        "pn",                 // Pitcairn Islands
        "pr",                 // Puerto Rico
        "ps",                 // Palestinian territories (PA-controlled West Bank and Gaza Strip)
        "pt",                 // Portugal
        "pw",                 // Palau
        "py",                 // Paraguay
        "qa",                 // Qatar
        "re",                 // Réunion
        "ro",                 // Romania
        "rs",                 // Serbia
        "ru",                 // Russia
        "rw",                 // Rwanda
        "sa",                 // Saudi Arabia
        "sb",                 // Solomon Islands
        "sc",                 // Seychelles
        "sd",                 // Sudan
        "se",                 // Sweden
        "sg",                 // Singapore
        "sh",                 // Saint Helena
        "si",                 // Slovenia
        "sj",                 // Svalbard and Jan Mayen Islands Not in use (Norwegian dependencies; see .no)
        "sk",                 // Slovakia
        "sl",                 // Sierra Leone
        "sm",                 // San Marino
        "sn",                 // Senegal
        "so",                 // Somalia
        "sr",                 // Suriname
        "st",                 // São Tomé and Príncipe
        "su",                 // Soviet Union (deprecated)
        "sv",                 // El Salvador
        "sx",                 // Sint Maarten
        "sy",                 // Syria
        "sz",                 // Swaziland
        "tc",                 // Turks and Caicos Islands
        "td",                 // Chad
        "tf",                 // French Southern and Antarctic Lands
        "tg",                 // Togo
        "th",                 // Thailand
        "tj",                 // Tajikistan
        "tk",                 // Tokelau
        "tl",                 // East Timor (deprecated old code)
        "tm",                 // Turkmenistan
        "tn",                 // Tunisia
        "to",                 // Tonga
        "tp",                 // East Timor
        "tr",                 // Turkey
        "tt",                 // Trinidad and Tobago
        "tv",                 // Tuvalu
        "tw",                 // Taiwan, Republic of China
        "tz",                 // Tanzania
        "ua",                 // Ukraine
        "ug",                 // Uganda
        "uk",                 // United Kingdom
        "us",                 // United States of America
        "uy",                 // Uruguay
        "uz",                 // Uzbekistan
        "va",                 // Vatican City State
        "vc",                 // Saint Vincent and the Grenadines
        "ve",                 // Venezuela
        "vg",                 // British Virgin Islands
        "vi",                 // U.S. Virgin Islands
        "vn",                 // Vietnam
        "vu",                 // Vanuatu
        "wf",                 // Wallis and Futuna
        "ws",                 // Samoa (formerly Western Samoa)
        "xn--3e0b707e", // 한국 KISA (Korea Internet &amp; Security Agency)
        "xn--45brj9c", // ভারত National Internet Exchange of India
        "xn--80ao21a", // қаз Association of IT Companies of Kazakhstan
        "xn--90a3ac", // срб Serbian National Internet Domain Registry (RNIDS)
        "xn--clchc0ea0b2g2a9gcd", // சிங்கப்பூர் Singapore Network Information Centre (SGNIC) Pte Ltd
        "xn--d1alf", // мкд Macedonian Academic Research Network Skopje
        "xn--fiqs8s", // 中国 China Internet Network Information Center
        "xn--fiqz9s", // 中國 China Internet Network Information Center
        "xn--fpcrj9c3d", // భారత్ National Internet Exchange of India
        "xn--fzc2c9e2c", // ලංකා LK Domain Registry
        "xn--gecrj9c", // ભારત National Internet Exchange of India
        "xn--h2brj9c", // भारत National Internet Exchange of India
        "xn--j1amh", // укр Ukrainian Network Information Centre (UANIC), Inc.
        "xn--j6w193g", // 香港 Hong Kong Internet Registration Corporation Ltd.
        "xn--kprw13d", // 台湾 Taiwan Network Information Center (TWNIC)
        "xn--kpry57d", // 台灣 Taiwan Network Information Center (TWNIC)
        "xn--l1acc", // мон Datacom Co.,Ltd
        "xn--lgbbat1ad8j", // الجزائر CERIST
        "xn--mgb9awbf", // عمان Telecommunications Regulatory Authority (TRA)
        "xn--mgba3a4f16a", // ایران Institute for Research in Fundamental Sciences (IPM)
        "xn--mgbaam7a8h", // امارات Telecommunications Regulatory Authority (TRA)
        "xn--mgbayh7gpa", // الاردن National Information Technology Center (NITC)
        "xn--mgbbh1a71e", // بھارت National Internet Exchange of India
        "xn--mgbc0a9azcg", // المغرب Agence Nationale de Réglementation des Télécommunications (ANRT)
        "xn--mgberp4a5d4ar", // السعودية Communications and Information Technology Commission
        "xn--mgbx4cd0ab", // مليسيا MYNIC Berhad
        "xn--node", // გე Information Technologies Development Center (ITDC)
        "xn--o3cw4h", // ไทย Thai Network Information Center Foundation
        "xn--ogbpf8fl", // سورية National Agency for Network Services (NANS)
        "xn--p1ai", // рф Coordination Center for TLD RU
        "xn--pgbs0dh", // تونس Agence Tunisienne d&#39;Internet
        "xn--s9brj9c", // ਭਾਰਤ National Internet Exchange of India
        "xn--wgbh1c", // مصر National Telecommunication Regulatory Authority - NTRA
        "xn--wgbl6a", // قطر Communications Regulatory Authority
        "xn--xkc2al3hye2a", // இலங்கை LK Domain Registry
        "xn--xkc2dl3a5ee0h", // இந்தியா National Internet Exchange of India
        "xn--yfro4i67o", // 新加坡 Singapore Network Information Centre (SGNIC) Pte Ltd
        "xn--ygbi2ammx", // فلسطين Ministry of Telecom &amp; Information Technology (MTIT)
        "ye",                 // Yemen
        "yt",                 // Mayotte
        "za",                 // South Africa
        "zm",                 // Zambia
        "zw",                 // Zimbabwe
    };

    // WARNING: this array MUST be sorted, others it cannot be searched reliably using binary search
    private static final String[] LOCAL_TLDS = new String[] {
       "localdomain",         // Also widely used as localhost.localdomain
       "localhost",           // RFC2606 defined
    };

    /**
     * Converts potentially Unicode input to punycode.
     * If conversion fails, returns the original input.
     * 
     * @param input the string to convert, not null
     * @return converted input, or original input if conversion fails
     */
    // Needed by UrlValidator
    static String unicodeToASCII(String input) {
        try {
            return /* java.net.IDN. */ toASCII(input);
        } catch (IllegalArgumentException e) { // input is not valid
            return input;
        }
    }

    // ================= Code needed for Java 1.4 and 1.5 compatibility ===============

    private static class IDNHolder {
        private static Method getMethod() {
            try {
                Class clazz = Class.forName("java.net.IDN", false, DomainValidator.class.getClassLoader());
                return clazz.getDeclaredMethod("toASCII", new Class[]{String.class});
            } catch (Exception e) {
              return null;
            }
        }
        private static final Method JAVA_NET_IDN_TO_ASCII = getMethod();
    }

    /*
     * Helper method to invoke java.net.IDN.toAscii(String).
     * Allows code to be compiled with Java 1.4 and 1.5 
     * @throws IllegalArgumentException if the input string doesn't conform to RFC 3490 specification
     */
    private static final String toASCII(String line) throws IllegalArgumentException {
//        java.net.IDN.toASCII(line); // Java 1.6+
        // implementation for Java 1.4 and 1.5
        // effectively this is done by IDN.toASCII but we want to skip the entire call
        if (isOnlyASCII(line)) {
            return line;
        }
        Method m = IDNHolder.JAVA_NET_IDN_TO_ASCII;
        if (m == null) { // avoid NPE
            return line;
        }
        try {
            return (String) m.invoke(null, new String[]{line.toLowerCase(Locale.ENGLISH)});
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e); // Should not happen
        } catch (InvocationTargetException e) {
            Throwable t = e.getCause();
            if (t instanceof IllegalArgumentException) { // this is expected from toASCII method
                throw (IllegalArgumentException) t;
            }
            throw new RuntimeException(e); // Should not happen
        }
    }

    /*
     * Check if input contains only ASCII
     * Treats null as all ASCII
     */
    private static boolean isOnlyASCII(String input) {
        if (input == null) {
            return true;
        }
        for(int i=0; i < input.length(); i++) {
            if (input.charAt(i) > 0x7F) {
                return false;
            }
        }
        return true;
    }

}
