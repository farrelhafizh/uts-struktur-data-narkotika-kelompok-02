package uts.model;

import uts.ds.SinglyLinkedList;

/** Hasil agregasi. */
public class Aggregate {
    public int jumlahPerkara;
    public double rataRataVonisBulan;
    public long totalDenda;
    public final SinglyLinkedList<Counter> distribusiJenis = new SinglyLinkedList<>();

    public String ringkas() {
        StringBuilder sb = new StringBuilder();
        sb.append("Jumlah perkara   : ").append(jumlahPerkara).append('\n');
        sb.append(String.format("Rata-rata vonis  : %.2f bulan%n", rataRataVonisBulan));
        sb.append(String.format("Total denda      : Rp%,d%n", totalDenda));
        sb.append("Distribusi jenis narkotika:\n");
        for (SinglyLinkedList.Node<Counter> n = distribusiJenis.headNode(); n != null; n = n.getNext())
            sb.append("  - ").append(n.getValue().getKey()).append(": ").append(n.getValue().getCount()).append('\n');
        return sb.toString();
    }
}
