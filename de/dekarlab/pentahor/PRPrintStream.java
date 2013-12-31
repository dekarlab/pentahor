package de.dekarlab.pentahor;

import java.io.OutputStream;
import java.io.PrintStream;

import org.pentaho.di.core.logging.LogChannelInterface;

public class PRPrintStream extends PrintStream {

	private LogChannelInterface log;

	public PRPrintStream(OutputStream out, LogChannelInterface log) {
		super(out);
		this.log = log;
	}

	/**
	 * Method println
	 * 
	 * @param
	 **/
	public void println(String string) {
		log.logBasic(string);
	}

	/**
	 * Method print
	 * 
	 * @param attribute
	 *            of the class).
	 **/
	public void print(String string) {
		log.logBasic(string);
	}
}
