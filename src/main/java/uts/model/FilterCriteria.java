package uts.model;

/** Kriteria filter gabungan (AND). Field null berarti tidak dipakai. */
public class FilterCriteria {
    public String jenisNarkotika;     // substring, tidak peka huruf besar/kecil
    public Double minBerat, maxBerat; // gram
    public String pasalTerbukti;      // substring
    public Integer minVonis, maxVonis; // bulan

    public boolean matches(Putusan p) {
        if (jenisNarkotika != null && !containsIgnoreCase(p.getJenisNarkotika(), jenisNarkotika)) return false;
        if (minBerat != null && p.getBeratBarangBuktiGram() < minBerat) return false;
        if (maxBerat != null && p.getBeratBarangBuktiGram() > maxBerat) return false;
        if (pasalTerbukti != null && !containsIgnoreCase(p.getPasalTerbukti(), pasalTerbukti)) return false;
        if (minVonis != null && p.getVonisBulan() < minVonis) return false;
        if (maxVonis != null && p.getVonisBulan() > maxVonis) return false;
        return true;
    }

    private static boolean containsIgnoreCase(String text, String part) {
        return text.toLowerCase().contains(part.toLowerCase());
    }

    @Override
    public String toString() {
        return "jenis=" + jenisNarkotika + ", berat=[" + minBerat + "," + maxBerat + "], pasal="
                + pasalTerbukti + ", vonis=[" + minVonis + "," + maxVonis + "]";
    }
}
