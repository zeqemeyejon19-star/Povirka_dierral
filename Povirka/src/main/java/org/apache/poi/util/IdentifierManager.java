package org.apache.poi.util;

import java.util.LinkedList;
import java.util.ListIterator;

/* JADX INFO: loaded from: classes.dex */
public class IdentifierManager {
    public static final long MAX_ID = 9223372036854775806L;
    public static final long MIN_ID = 0;
    private final long lowerbound;
    private LinkedList<Segment> segments;
    private final long upperbound;

    public IdentifierManager(long lowerbound, long upperbound) {
        if (lowerbound > upperbound) {
            throw new IllegalArgumentException("lowerbound must not be greater than upperbound, had " + lowerbound + " and " + upperbound);
        }
        if (lowerbound < 0) {
            String message = "lowerbound must be greater than or equal to " + Long.toString(0L);
            throw new IllegalArgumentException(message);
        }
        if (upperbound > MAX_ID) {
            throw new IllegalArgumentException("upperbound must be less than or equal to " + Long.toString(MAX_ID) + " but had " + upperbound);
        }
        this.lowerbound = lowerbound;
        this.upperbound = upperbound;
        LinkedList<Segment> linkedList = new LinkedList<>();
        this.segments = linkedList;
        linkedList.add(new Segment(lowerbound, upperbound));
    }

    public long reserve(long id) {
        if (id < this.lowerbound || id > this.upperbound) {
            throw new IllegalArgumentException("Value for parameter 'id' was out of bounds, had " + id + ", but should be within [" + this.lowerbound + ":" + this.upperbound + "]");
        }
        verifyIdentifiersLeft();
        if (id == this.upperbound) {
            Segment lastSegment = this.segments.getLast();
            long j = lastSegment.end;
            long j2 = this.upperbound;
            if (j == j2) {
                lastSegment.end = j2 - 1;
                if (lastSegment.start > lastSegment.end) {
                    this.segments.removeLast();
                }
                return id;
            }
            return reserveNew();
        }
        if (id == this.lowerbound) {
            Segment firstSegment = this.segments.getFirst();
            long j3 = firstSegment.start;
            long j4 = this.lowerbound;
            if (j3 == j4) {
                firstSegment.start = j4 + 1;
                if (firstSegment.end < firstSegment.start) {
                    this.segments.removeFirst();
                }
                return id;
            }
            return reserveNew();
        }
        ListIterator<Segment> iter = this.segments.listIterator();
        while (true) {
            if (!iter.hasNext()) {
                break;
            }
            Segment segment = iter.next();
            if (segment.end >= id) {
                if (segment.start <= id) {
                    if (segment.start == id) {
                        segment.start = 1 + id;
                        if (segment.end < segment.start) {
                            iter.remove();
                        }
                        return id;
                    }
                    if (segment.end == id) {
                        segment.end = id - 1;
                        if (segment.start > segment.end) {
                            iter.remove();
                        }
                        return id;
                    }
                    iter.add(new Segment(id + 1, segment.end));
                    segment.end = id - 1;
                    return id;
                }
            }
        }
        return reserveNew();
    }

    public long reserveNew() {
        verifyIdentifiersLeft();
        Segment segment = this.segments.getFirst();
        long result = segment.start;
        segment.start++;
        if (segment.start > segment.end) {
            this.segments.removeFirst();
        }
        return result;
    }

    public boolean release(long id) {
        long j = this.lowerbound;
        if (id >= j) {
            long j2 = this.upperbound;
            if (id <= j2) {
                if (id == j2) {
                    Segment lastSegment = this.segments.getLast();
                    long j3 = lastSegment.end;
                    long j4 = this.upperbound;
                    if (j3 == j4 - 1) {
                        lastSegment.end = j4;
                        return true;
                    }
                    if (lastSegment.end == this.upperbound) {
                        return false;
                    }
                    LinkedList<Segment> linkedList = this.segments;
                    long j5 = this.upperbound;
                    linkedList.add(new Segment(j5, j5));
                    return true;
                }
                if (id == j) {
                    Segment firstSegment = this.segments.getFirst();
                    long j6 = firstSegment.start;
                    long j7 = this.lowerbound;
                    if (j6 == 1 + j7) {
                        firstSegment.start = j7;
                        return true;
                    }
                    if (firstSegment.start == this.lowerbound) {
                        return false;
                    }
                    LinkedList<Segment> linkedList2 = this.segments;
                    long j8 = this.lowerbound;
                    linkedList2.addFirst(new Segment(j8, j8));
                    return true;
                }
                long higher = id + 1;
                long lower = id - 1;
                ListIterator<Segment> iter = this.segments.listIterator();
                while (true) {
                    if (!iter.hasNext()) {
                        break;
                    }
                    Segment segment = iter.next();
                    if (segment.end >= lower) {
                        if (segment.start > higher) {
                            iter.previous();
                            iter.add(new Segment(id, id));
                            return true;
                        }
                        if (segment.start == higher) {
                            segment.start = id;
                            return true;
                        }
                        if (segment.end == lower) {
                            segment.end = id;
                            if (iter.hasNext()) {
                                Segment next = iter.next();
                                if (next.start == segment.end + 1) {
                                    segment.end = next.end;
                                    iter.remove();
                                    return true;
                                }
                                return true;
                            }
                            return true;
                        }
                    }
                }
                return false;
            }
        }
        throw new IllegalArgumentException("Value for parameter 'id' was out of bounds, had " + id + ", but should be within [" + this.lowerbound + ":" + this.upperbound + "]");
    }

    public long getRemainingIdentifiers() {
        long result = 0;
        for (Segment segment : this.segments) {
            result = segment.end + (result - segment.start) + 1;
        }
        return result;
    }

    private void verifyIdentifiersLeft() {
        if (this.segments.isEmpty()) {
            throw new IllegalStateException("No identifiers left");
        }
    }

    private static class Segment {
        public long end;
        public long start;

        public Segment(long start, long end) {
            this.start = start;
            this.end = end;
        }

        public String toString() {
            return "[" + this.start + "; " + this.end + "]";
        }
    }
}
