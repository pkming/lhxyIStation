package org.apache.tools.ant.taskdefs.optional.testing;

import android.accounts.GrantCredentialsPermissionActivity;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskAdapter;
import org.apache.tools.ant.taskdefs.Parallel;
import org.apache.tools.ant.taskdefs.Sequential;
import org.apache.tools.ant.taskdefs.WaitFor;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.taskdefs.condition.ConditionBase;
import org.apache.tools.ant.util.WorkerAnt;

/* JADX INFO: loaded from: classes3.dex */
public class Funtest extends Task {
    public static final String APPLICATION_EXCEPTION = "Application Exception";
    public static final String APPLICATION_FORCIBLY_SHUT_DOWN = "Application forcibly shut down";
    public static final String SHUTDOWN_INTERRUPTED = "Shutdown interrupted";
    public static final String SKIPPING_TESTS = "Condition failed -skipping tests";
    public static final String TEARDOWN_EXCEPTION = "Teardown Exception";
    public static final String WARN_OVERRIDING = "Overriding previous definition of ";
    private Sequential application;
    private BuildException applicationException;
    private BlockFor block;
    private NestedCondition condition;
    private String failureProperty;
    private Sequential reporting;
    private Sequential setup;
    private BuildException taskException;
    private Sequential teardown;
    private BuildException teardownException;
    private BuildException testException;
    private Sequential tests;
    private Parallel timedTests;
    private long timeout;
    private long timeoutUnitMultiplier = 1;
    private long shutdownTime = 10000;
    private long shutdownUnitMultiplier = 1;
    private String failureMessage = "Tests failed";
    private boolean failOnTeardownErrors = true;

    private void logOverride(String str, Object obj) {
        if (obj != null) {
            log("Overriding previous definition of <" + str + '>', 2);
        }
    }

    public ConditionBase createCondition() {
        logOverride("condition", this.condition);
        NestedCondition nestedCondition = new NestedCondition();
        this.condition = nestedCondition;
        return nestedCondition;
    }

    public void addApplication(Sequential sequential) {
        logOverride(GrantCredentialsPermissionActivity.EXTRAS_PACKAGES, this.application);
        this.application = sequential;
    }

    public void addSetup(Sequential sequential) {
        logOverride("setup", this.setup);
        this.setup = sequential;
    }

    public void addBlock(BlockFor blockFor) {
        logOverride("block", this.block);
        this.block = blockFor;
    }

    public void addTests(Sequential sequential) {
        logOverride("tests", this.tests);
        this.tests = sequential;
    }

    public void addReporting(Sequential sequential) {
        logOverride("reporting", this.reporting);
        this.reporting = sequential;
    }

    public void addTeardown(Sequential sequential) {
        logOverride("teardown", this.teardown);
        this.teardown = sequential;
    }

    public void setFailOnTeardownErrors(boolean z) {
        this.failOnTeardownErrors = z;
    }

    public void setFailureMessage(String str) {
        this.failureMessage = str;
    }

    public void setFailureProperty(String str) {
        this.failureProperty = str;
    }

    public void setShutdownTime(long j) {
        this.shutdownTime = j;
    }

    public void setTimeout(long j) {
        this.timeout = j;
    }

    public void setTimeoutUnit(WaitFor.Unit unit) {
        this.timeoutUnitMultiplier = unit.getMultiplier();
    }

    public void setShutdownUnit(WaitFor.Unit unit) {
        this.shutdownUnitMultiplier = unit.getMultiplier();
    }

    public BuildException getApplicationException() {
        return this.applicationException;
    }

    public BuildException getTeardownException() {
        return this.teardownException;
    }

    public BuildException getTestException() {
        return this.testException;
    }

    public BuildException getTaskException() {
        return this.taskException;
    }

    private void bind(Task task) {
        task.bindToOwner(this);
        task.init();
    }

    private Parallel newParallel(long j) {
        Parallel parallel = new Parallel();
        bind(parallel);
        parallel.setFailOnAny(true);
        parallel.setTimeout(j);
        return parallel;
    }

    private Parallel newParallel(long j, Task task) {
        Parallel parallelNewParallel = newParallel(j);
        parallelNewParallel.addTask(task);
        return parallelNewParallel;
    }

    private void validateTask(Task task, String str) {
        if (task != null && task.getProject() == null) {
            throw new BuildException(str + " task is not bound to the project" + task);
        }
    }

    /* JADX WARN: Finally extract failed */
    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        validateTask(this.setup, "setup");
        validateTask(this.application, GrantCredentialsPermissionActivity.EXTRAS_PACKAGES);
        validateTask(this.tests, "tests");
        validateTask(this.reporting, "reporting");
        validateTask(this.teardown, "teardown");
        NestedCondition nestedCondition = this.condition;
        if (nestedCondition != null && !nestedCondition.eval()) {
            log(SKIPPING_TESTS);
            return;
        }
        long j = this.timeout * this.timeoutUnitMultiplier;
        Parallel parallelNewParallel = newParallel(j);
        WorkerAnt workerAnt = new WorkerAnt(parallelNewParallel, null);
        Task task = this.application;
        if (task != null) {
            parallelNewParallel.addTask(task);
        }
        long jCalculateMaxWaitMillis = 0;
        Sequential sequential = new Sequential();
        bind(sequential);
        if (this.block != null) {
            Task taskAdapter = new TaskAdapter(this.block);
            taskAdapter.bindToOwner(this);
            validateTask(taskAdapter, "block");
            sequential.addTask(taskAdapter);
            jCalculateMaxWaitMillis = this.block.calculateMaxWaitMillis();
        }
        Sequential sequential2 = this.tests;
        if (sequential2 != null) {
            sequential.addTask(sequential2);
            jCalculateMaxWaitMillis += j;
        }
        Sequential sequential3 = this.reporting;
        if (sequential3 != null) {
            sequential.addTask(sequential3);
            jCalculateMaxWaitMillis += j;
        }
        this.timedTests = newParallel(jCalculateMaxWaitMillis, sequential);
        try {
            try {
                try {
                    Task task2 = this.setup;
                    if (task2 != null) {
                        newParallel(j, task2).execute();
                    }
                    workerAnt.start();
                    this.timedTests.execute();
                    Task task3 = this.teardown;
                    if (task3 != null) {
                        newParallel(j, task3).execute();
                    }
                } catch (BuildException e) {
                    this.testException = e;
                    Task task4 = this.teardown;
                    if (task4 != null) {
                        newParallel(j, task4).execute();
                    }
                }
            } catch (BuildException e2) {
                this.teardownException = e2;
            }
            try {
                long j2 = this.shutdownTime * this.shutdownUnitMultiplier;
                workerAnt.waitUntilFinished(j2);
                if (workerAnt.isAlive()) {
                    log(APPLICATION_FORCIBLY_SHUT_DOWN, 1);
                    workerAnt.interrupt();
                    workerAnt.waitUntilFinished(j2);
                }
            } catch (InterruptedException e3) {
                log(SHUTDOWN_INTERRUPTED, e3, 3);
            }
            this.applicationException = workerAnt.getBuildException();
            processExceptions();
        } catch (Throwable th) {
            Task task5 = this.teardown;
            if (task5 != null) {
                try {
                    newParallel(j, task5).execute();
                } catch (BuildException e4) {
                    this.teardownException = e4;
                }
            }
            throw th;
        }
    }

    protected void processExceptions() {
        BuildException buildException = this.testException;
        this.taskException = buildException;
        BuildException buildException2 = this.applicationException;
        if (buildException2 != null) {
            if (buildException == null || (buildException instanceof BuildTimeoutException)) {
                this.taskException = buildException2;
            } else {
                ignoringThrowable(APPLICATION_EXCEPTION, buildException2);
            }
        }
        BuildException buildException3 = this.teardownException;
        if (buildException3 != null) {
            if (this.taskException == null && this.failOnTeardownErrors) {
                this.taskException = buildException3;
            } else {
                ignoringThrowable(TEARDOWN_EXCEPTION, buildException3);
            }
        }
        if (this.failureProperty != null && getProject().getProperty(this.failureProperty) != null) {
            log(this.failureMessage);
            if (this.taskException == null) {
                this.taskException = new BuildException(this.failureMessage);
            }
        }
        BuildException buildException4 = this.taskException;
        if (buildException4 != null) {
            throw buildException4;
        }
    }

    protected void ignoringThrowable(String str, Throwable th) {
        log(str + ": " + th.toString(), th, 1);
    }

    private static class NestedCondition extends ConditionBase implements Condition {
        private NestedCondition() {
        }

        @Override // org.apache.tools.ant.taskdefs.condition.Condition
        public boolean eval() {
            if (countConditions() != 1) {
                throw new BuildException("A single nested condition is required.");
            }
            return ((Condition) getConditions().nextElement()).eval();
        }
    }
}
