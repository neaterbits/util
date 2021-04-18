package org.jutils.concurrency.scheduling;

public enum Constraint {
	IO,
	NETWORK,
	CPU,
	CPU_IO_CACHED; // most often disk cached
}
