package uts;

import org.junit.jupiter.api.Test;
import uts.ds.ArrayStack;

import static org.junit.jupiter.api.Assertions.*;

class ArrayStackTest {

    @Test void lifo() {
        ArrayStack<Integer> s = new ArrayStack<>();
        s.push(1); s.push(2); s.push(3);
        assertEquals(3, s.pop());
        assertEquals(2, s.peek());
        s.validateInvariant();
    }

    @Test void resizeOtomatis() {
        ArrayStack<Integer> s = new ArrayStack<>();
        for (int i = 0; i < 100; i++) s.push(i);
        assertEquals(100, s.size());
        assertEquals(99, s.peek());
        s.validateInvariant();
    }

    @Test void popKosongMelempar() {
        ArrayStack<Integer> s = new ArrayStack<>();
        assertThrows(IllegalStateException.class, s::pop);
        assertThrows(IllegalStateException.class, s::peek);
    }

    @Test void searchPreserveTidakMengubahUrutan() {
        ArrayStack<Integer> s = new ArrayStack<>();
        for (int i = 1; i <= 6; i++) s.push(i);
        int depth = s.searchPreserve(x -> x == 2);
        assertEquals(4, depth);
        assertEquals(5, s.getLastSearchSteps());
        assertEquals(6, s.size());
        for (int d = 0; d < 6; d++) assertEquals(6 - d, s.get(d));
        assertEquals(-1, s.searchPreserve(x -> x == 99));
        assertEquals(6, s.size());
        for (int d = 0; d < 6; d++) assertEquals(6 - d, s.get(d));
        s.validateInvariant();
    }

    @Test void removeSelainPuncakDitolak() {
        ArrayStack<Integer> s = new ArrayStack<>();
        s.push(1); s.push(2);
        assertThrows(UnsupportedOperationException.class, () -> s.remove(1));
        assertEquals(2, s.remove(0));
    }

    @Test void clearMembersihkanSlot() {
        ArrayStack<Integer> s = new ArrayStack<>();
        s.push(1); s.push(2);
        s.clear();
        assertTrue(s.isEmpty());
        s.validateInvariant();
    }
}
