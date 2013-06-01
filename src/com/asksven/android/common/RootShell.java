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
public class RootShell
{
	static RootShell m_instance = null;
	static Shell m_shell = null;
	private RootShell()
	{
	}

	public static RootShell getInstance()
	{
		if (m_instance == null)
		{
			m_instance = new RootShell();
			try
			{
				m_shell = RootTools.getShell(true);
			}
			catch (Exception e)
			{
				m_shell = null;
			}
		}
		
		return m_instance;
	}
	
//	public List<String> run1(String command)
//	{
//		return Shell.SU.run(command);
//	}
	
	public synchronized List<String> run(String command)
	{
		final List<String> res = new ArrayList<String>();
		
		if (!RootTools.isRootAvailable())
		{
			return res;
		}
		
		if (m_shell == null)
		{
			// reopen if for whatever reason the shell got closed
			RootShell.getInstance();
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
			RootTools.getShell(true).add(shellCommand).waitForFinish();
		}
		catch (Exception e)
		{
			
		}
		
		return res;
		
	}
	
	public boolean rooted()
	{
		return RootTools.isRootAvailable();
	}
	
}
