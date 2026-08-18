package org.apache.commons.net.nntp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public class Threader {
    private int bogusIdCount = 0;
    private HashMap<String, ThreadContainer> idTable;
    private ThreadContainer root;

    public Threadable thread(List<? extends Threadable> list) {
        return thread((Iterable<? extends Threadable>) list);
    }

    public Threadable thread(Iterable<? extends Threadable> iterable) {
        if (iterable == null) {
            return null;
        }
        this.idTable = new HashMap<>();
        for (Threadable threadable : iterable) {
            if (!threadable.isDummy()) {
                buildContainer(threadable);
            }
        }
        this.root = findRootSet();
        this.idTable.clear();
        this.idTable = null;
        pruneEmptyContainers(this.root);
        this.root.reverseChildren();
        gatherSubjects();
        if (this.root.next != null) {
            throw new RuntimeException("root node has a next:" + this.root);
        }
        for (ThreadContainer threadContainer = this.root.child; threadContainer != null; threadContainer = threadContainer.next) {
            if (threadContainer.threadable == null) {
                threadContainer.threadable = threadContainer.child.threadable.makeDummy();
            }
        }
        Threadable threadable2 = this.root.child == null ? null : this.root.child.threadable;
        this.root.flush();
        this.root = null;
        return threadable2;
    }

    private void buildContainer(Threadable threadable) {
        String strMessageThreadId = threadable.messageThreadId();
        ThreadContainer threadContainer = this.idTable.get(strMessageThreadId);
        if (threadContainer != null) {
            if (threadContainer.threadable != null) {
                StringBuilder sbAppend = new StringBuilder().append("<Bogus-id:");
                int i = this.bogusIdCount;
                this.bogusIdCount = i + 1;
                strMessageThreadId = sbAppend.append(i).append(">").toString();
                threadContainer = null;
            } else {
                threadContainer.threadable = threadable;
            }
        }
        if (threadContainer == null) {
            threadContainer = new ThreadContainer();
            threadContainer.threadable = threadable;
            this.idTable.put(strMessageThreadId, threadContainer);
        }
        String[] strArrMessageThreadReferences = threadable.messageThreadReferences();
        int length = strArrMessageThreadReferences.length;
        int i2 = 0;
        ThreadContainer threadContainer2 = null;
        while (i2 < length) {
            String str = strArrMessageThreadReferences[i2];
            ThreadContainer threadContainer3 = this.idTable.get(str);
            if (threadContainer3 == null) {
                threadContainer3 = new ThreadContainer();
                this.idTable.put(str, threadContainer3);
            }
            if (threadContainer2 != null && threadContainer3.parent == null && threadContainer2 != threadContainer3 && !threadContainer3.findChild(threadContainer2)) {
                threadContainer3.parent = threadContainer2;
                threadContainer3.next = threadContainer2.child;
                threadContainer2.child = threadContainer3;
            }
            i2++;
            threadContainer2 = threadContainer3;
        }
        if (threadContainer2 != null && (threadContainer2 == threadContainer || threadContainer.findChild(threadContainer2))) {
            threadContainer2 = null;
        }
        if (threadContainer.parent != null) {
            ThreadContainer threadContainer4 = threadContainer.parent.child;
            ThreadContainer threadContainer5 = null;
            while (threadContainer4 != null && threadContainer4 != threadContainer) {
                threadContainer5 = threadContainer4;
                threadContainer4 = threadContainer4.next;
            }
            if (threadContainer4 == null) {
                throw new RuntimeException("Didnt find " + threadContainer + " in parent" + threadContainer.parent);
            }
            if (threadContainer5 == null) {
                threadContainer.parent.child = threadContainer.next;
            } else {
                threadContainer5.next = threadContainer.next;
            }
            threadContainer.next = null;
            threadContainer.parent = null;
        }
        if (threadContainer2 != null) {
            threadContainer.parent = threadContainer2;
            threadContainer.next = threadContainer2.child;
            threadContainer2.child = threadContainer;
        }
    }

    private ThreadContainer findRootSet() {
        ThreadContainer threadContainer = new ThreadContainer();
        Iterator<String> it = this.idTable.keySet().iterator();
        while (it.hasNext()) {
            ThreadContainer threadContainer2 = this.idTable.get(it.next());
            if (threadContainer2.parent == null) {
                if (threadContainer2.next != null) {
                    throw new RuntimeException("c.next is " + threadContainer2.next.toString());
                }
                threadContainer2.next = threadContainer.child;
                threadContainer.child = threadContainer2;
            }
        }
        return threadContainer;
    }

    private void pruneEmptyContainers(ThreadContainer threadContainer) {
        ThreadContainer threadContainer2 = threadContainer.child;
        ThreadContainer threadContainer3 = threadContainer2.next;
        ThreadContainer threadContainer4 = null;
        while (threadContainer2 != null) {
            if (threadContainer2.threadable == null && threadContainer2.child == null) {
                if (threadContainer4 == null) {
                    threadContainer.child = threadContainer2.next;
                } else {
                    threadContainer4.next = threadContainer2.next;
                }
            } else if (threadContainer2.threadable == null && threadContainer2.child != null && (threadContainer2.parent != null || threadContainer2.child.next == null)) {
                threadContainer3 = threadContainer2.child;
                if (threadContainer4 == null) {
                    threadContainer.child = threadContainer3;
                } else {
                    threadContainer4.next = threadContainer3;
                }
                ThreadContainer threadContainer5 = threadContainer3;
                while (threadContainer5.next != null) {
                    threadContainer5.parent = threadContainer2.parent;
                    threadContainer5 = threadContainer5.next;
                }
                threadContainer5.parent = threadContainer2.parent;
                threadContainer5.next = threadContainer2.next;
            } else {
                if (threadContainer2.child != null) {
                    pruneEmptyContainers(threadContainer2);
                }
                threadContainer4 = threadContainer2;
            }
            threadContainer2 = threadContainer3;
            threadContainer3 = threadContainer2 == null ? null : threadContainer2.next;
        }
    }

    private void gatherSubjects() {
        ThreadContainer threadContainer;
        ThreadContainer threadContainer2;
        int i = 0;
        int i2 = 0;
        for (ThreadContainer threadContainer3 = this.root.child; threadContainer3 != null; threadContainer3 = threadContainer3.next) {
            i2++;
        }
        HashMap map = new HashMap((int) (((double) i2) * 1.2d), 0.9f);
        for (ThreadContainer threadContainer4 = this.root.child; threadContainer4 != null; threadContainer4 = threadContainer4.next) {
            Threadable threadable = threadContainer4.threadable;
            if (threadable == null) {
                threadable = threadContainer4.child.threadable;
            }
            String strSimplifiedSubject = threadable.simplifiedSubject();
            if (strSimplifiedSubject != null && strSimplifiedSubject.length() != 0 && ((threadContainer2 = (ThreadContainer) map.get(strSimplifiedSubject)) == null || ((threadContainer4.threadable == null && threadContainer2.threadable != null) || (threadContainer2.threadable != null && threadContainer2.threadable.subjectIsReply() && threadContainer4.threadable != null && !threadContainer4.threadable.subjectIsReply())))) {
                map.put(strSimplifiedSubject, threadContainer4);
                i++;
            }
        }
        if (i == 0) {
            return;
        }
        ThreadContainer threadContainer5 = this.root.child;
        ThreadContainer threadContainer6 = threadContainer5.next;
        ThreadContainer threadContainer7 = null;
        while (threadContainer5 != null) {
            Threadable threadable2 = threadContainer5.threadable;
            if (threadable2 == null) {
                threadable2 = threadContainer5.child.threadable;
            }
            String strSimplifiedSubject2 = threadable2.simplifiedSubject();
            if (strSimplifiedSubject2 == null || strSimplifiedSubject2.length() == 0 || (threadContainer = (ThreadContainer) map.get(strSimplifiedSubject2)) == threadContainer5) {
                threadContainer7 = threadContainer5;
            } else {
                if (threadContainer7 == null) {
                    this.root.child = threadContainer5.next;
                } else {
                    threadContainer7.next = threadContainer5.next;
                }
                threadContainer5.next = null;
                if (threadContainer.threadable == null && threadContainer5.threadable == null) {
                    ThreadContainer threadContainer8 = threadContainer.child;
                    while (threadContainer8 != null && threadContainer8.next != null) {
                        threadContainer8 = threadContainer8.next;
                    }
                    if (threadContainer8 != null) {
                        threadContainer8.next = threadContainer5.child;
                    }
                    for (ThreadContainer threadContainer9 = threadContainer5.child; threadContainer9 != null; threadContainer9 = threadContainer9.next) {
                        threadContainer9.parent = threadContainer;
                    }
                    threadContainer5.child = null;
                } else if (threadContainer.threadable == null || (threadContainer5.threadable != null && threadContainer5.threadable.subjectIsReply() && !threadContainer.threadable.subjectIsReply())) {
                    threadContainer5.parent = threadContainer;
                    threadContainer5.next = threadContainer.child;
                    threadContainer.child = threadContainer5;
                } else {
                    ThreadContainer threadContainer10 = new ThreadContainer();
                    threadContainer10.threadable = threadContainer.threadable;
                    threadContainer10.child = threadContainer.child;
                    for (ThreadContainer threadContainer11 = threadContainer10.child; threadContainer11 != null; threadContainer11 = threadContainer11.next) {
                        threadContainer11.parent = threadContainer10;
                    }
                    threadContainer.threadable = null;
                    threadContainer.child = null;
                    threadContainer5.parent = threadContainer;
                    threadContainer10.parent = threadContainer;
                    threadContainer.child = threadContainer5;
                    threadContainer5.next = threadContainer10;
                }
            }
            ThreadContainer threadContainer12 = threadContainer6;
            threadContainer6 = threadContainer6 == null ? null : threadContainer6.next;
            threadContainer5 = threadContainer12;
        }
        map.clear();
    }

    @Deprecated
    public Threadable thread(Threadable[] threadableArr) {
        return thread(Arrays.asList(threadableArr));
    }
}
