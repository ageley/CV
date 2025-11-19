import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.YEARS;

static final String CURRENT_TIMEZONE = "Europe/Belgrade";
static final LocalDate BIRTH_DATE = LocalDate.of(1990, 7, 10);
static final LocalDate CAREER_START = LocalDate.of(2012, 10, 1);
static final LocalDate CURRENT_ROLE_START = LocalDate.of(2019, 12, 1);

void main() throws IOException {
    LocalDate today = LocalDate.now(ZoneId.of(CURRENT_TIMEZONE));
    Path cv = Path.of("README.md");
    String originalCv = Files.readString(cv);
    String updatedCv = updateTerms(originalCv, today);

    if (!updatedCv.equals(originalCv)) {
        Files.writeString(cv, updatedCv);
        System.out.printf("Updated README.md for %s%n", today);
    } else {
        IO.println("README.md is already up to date");
    }
}

static String updateTerms(String text, LocalDate today) {
    text = replaceTerm(text, "age", years(YEARS.between(BIRTH_DATE, today)));
    text = replaceTerm(text, "exp", years(YEARS.between(CAREER_START, today)));
    return replaceTerm(text, "tenure", tenureDuration(MONTHS.between(CURRENT_ROLE_START, today)));
}

static String replaceTerm(String text, String name, String value) {
    String start = String.format("<!-- %s:start -->", name);
    String end = String.format("<!-- %s:end -->", name);
    return text.replaceAll(String.format("(?s)%s.*?%s", start, end), String.format("%s%s%s", start, value, end));
}

static String tenureDuration(long totalMonths) {
    long wholeYears = totalMonths / 12;
    long remainingMonths = totalMonths % 12 + 1;
    return wholeYears == 0
            ? months(totalMonths)
            : remainingMonths == 0
              ? years(wholeYears)
              : String.format("%s %s", yrs(wholeYears), mos(remainingMonths));
}

static String years(long duration) {
    return new Term(duration, "year", "years").toString();
}

static String yrs(long duration) {
    return new Term(duration, "yr", "yrs").toString();
}

static String months(long duration) {
    return new Term(duration, "month", "months").toString();
}

static String mos(long duration) {
    return new Term(duration, "mo", "mos").toString();
}

record Term(long duration, String singleUnitName, String pluralUnitName) {
    @Override
    public String toString() {
        return String.format("%s %s", duration, duration == 1 ? singleUnitName : pluralUnitName);
    }
}