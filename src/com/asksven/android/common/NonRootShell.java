/**
 * 
 */
package com.asksven.android.common;

import java.util.ArrayList;
import java.util.List;

//import com.asksven.andoid.common.contrib.Shell;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.execution.Command;
import com.stericson.RootTools.execution.Shell;

/**
 * @author sven
 * Sigleton performing su operations
 *
 */
public class NonRootShell
{
	static NonRootShell m_instance = null;
	static Shell m_shell = null;
	private NonRootShell()
	{
	}

	public static NonRootShell getInstance()
	{
		if (m_instance == null)
		{
			m_instance = new NonRootShell();
			try
			{
				m_shell = RootTools.getShell(false);
			}
			catch (Exception e)
			{
				m_shell = null;
			}
		}
		
		return m_instance;
	}
	
	public synchronized List<String> run(String command)
	{
		final List<String> res = new ArrayList<String>();
		
		if (m_shell == null)
		{
			// reopen if for whatever reason the shell got closed
			NonRootShell.getInstance();
		}
		Command shellCommand = new Command(0, command)
		{
		        @Override
		        public void output(int id, String line)
		        {
		        	res.add(line);
		        }
		};
		try
		{
			m_shell.add(shellCommand).waitForFinish();
		}
		catch (Exception e)
		{
			
		}
		
		return res;
		
	}
	
}
