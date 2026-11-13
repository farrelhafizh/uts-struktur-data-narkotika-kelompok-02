package uts;

import org.junit.jupiter.api.Test;
import uts.ds.SinglyLinkedList;

import static org.junit.jupiter.api.Assertions.*;

class SinglyLinkedListTest {

    private SinglyLinkedList<Integer> isi(int n) {
        SinglyLinkedList<Integer> l = new SinglyLinkedList<>();
        for (int i = 1; i <= n; i++) l.addLast(i);
        return l;
    }

    @Test void kosongValid() {
        SinglyLinkedList<Integer> l = new SinglyLinkedList<>();
        l.validateInvariant();
        assertTrue(l.isEmpty());
    }

    @Test void addFirstDanAddLast() {
        SinglyLinkedList<Integer> l = new SinglyLinkedList<>();
        l.addFirst(2); l.addFirst(1); l.addLast(3);
        assertEquals(1, l.get(0));
        assertEquals(3, l.get(2));
        assertEquals(3, l.size());
        l.validateInvariant();
    }

    @Test void insertDiBatas() {
        SinglyLinkedList<Integer> l = isi(3);
        l.insert(0, 0); l.insert(4, 99); l.insert(2, 50);
        assertEquals(6, l.size());
        assertEquals(0, l.get(0));
        assertEquals(50, l.get(2));
        assertEquals(99, l.get(5));
        l.validateInvariant();
        assertThrows(IndexOutOfBoundsException.class, () -> l.insert(7, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> l.insert(-1, 1));
    }

    @Test void hapusPertamaTengahTerakhir() {
        SinglyLinkedList<Integer> l = isi(5);
        assertEquals(1, l.remove(0));
        l.validateInvariant();
        assertEquals(3, l.remove(1));
        l.validateInvariant();
        assertEquals(5, l.remove(l.size() - 1));
        l.validateInvariant();
        assertEquals(2, l.size());
        // tail harus pindah: addLast tetap tersambung
        l.addLast(7);
        assertEquals(7, l.get(l.size() - 1));
        l.validateInvariant();
    }

    @Test void hapusSampaiKosong() {
        SinglyLinkedList<Integer> l = isi(1);
        l.remove(0);
        l.validateInvariant();
        assertTrue(l.isEmpty());
        l.addLast(1);
        l.validateInvariant();
    }

    @Test void hapusDiLuarBatas() {
        SinglyLinkedList<Integer> l = isi(2);
        assertThrows(IndexOutOfBoundsException.class, () -> l.remove(2));
        assertThrows(IndexOutOfBoundsException.class, () -> l.get(-1));
        assertEquals(2, l.size());
        l.validateInvariant();
    }

    @Test void searchMelaporkanLangkah() {
        SinglyLinkedList<Integer> l = isi(10);
        assertEquals(6, l.search(x -> x == 7));
        assertEquals(7, l.getLastSearchSteps());
        assertEquals(-1, l.search(x -> x == 99));
        assertEquals(10, l.getLastSearchSteps());
    }

    @Test void containsDanClear() {
        SinglyLinkedList<Integer> l = isi(4);
        assertTrue(l.contains(3));
        assertFalse(l.contains(10));
        l.clear();
        assertEquals(0, l.size());
        l.validateInvariant();
    }

    @Test void filterTidakMengubahAsli() {
        SinglyLinkedList<Integer> l = isi(10);
        SinglyLinkedList<Integer> genap = l.filter(x -> x % 2 == 0);
        assertEquals(5, genap.size());
        assertEquals(10, l.size());
        genap.validateInvariant();
        l.validateInvariant();
    }

    @Test void lima50Elemen() {
        SinglyLinkedList<Integer> l = isi(50);
        assertEquals(50, l.size());
        l.validateInvariant();
    }
}
