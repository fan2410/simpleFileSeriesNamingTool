import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;


public class rawSub {
	
	static volatile boolean isAlphabetExtension = false;
	static volatile String orgName = "";
	
	public static void main(String[] args) {
		JPanel panel = new JPanel();
		JLabel label = new JLabel("Enter a password:");
		JPasswordField pass = new JPasswordField(10);
		panel.add(label);
		panel.add(pass);
		String[] options4PwdPanel = new String[]{"OK", "Cancel"};
		int option = JOptionPane.showOptionDialog(null, panel, "Login",
		                         JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
		                         null, options4PwdPanel, options4PwdPanel[0]);
		String tmpStr = new SimpleDateFormat("ddMMyyyymm").format(new Date());
		int tmpInt = Integer.parseInt(tmpStr.substring(8,10));
		tmpStr = Integer.toString(Integer.parseInt(tmpStr.substring(0,2)) + tmpInt) + 
					Integer.toString(Integer.parseInt(tmpStr.substring(2,4)) + tmpInt) +
					Integer.toString(Integer.parseInt(tmpStr.substring(6,8)) + tmpInt);
		if(option == 0 && tmpStr.equals(new String(pass.getPassword())))
		{
		    JPanel pwdPanel = new JPanel();
			pwdPanel.setLayout(new GridLayout(2,2));
			JLabel pwdLabel = new JLabel("1st Code:");
			JPasswordField pwdField = new JPasswordField(10);
			JPasswordField pwdConfirmField = new JPasswordField(10);
			pwdPanel.add(pwdLabel);
			pwdPanel.add(pwdField);
			pwdPanel.add(new JLabel("2nd Code:"));
			pwdPanel.add(pwdConfirmField);
			int pwdPanelSelectCode = -1; 
			String inpPwd = "";
			while (!(pwdPanelSelectCode == 0 && !inpPwd.isEmpty())) {
				pwdPanelSelectCode = JOptionPane.showOptionDialog(null, pwdPanel, "Welcome",
	                    		JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
	                    		null, options4PwdPanel, options4PwdPanel[0]);
				inpPwd = new String(pwdConfirmField.getPassword());
				if (!new String(pwdField.getPassword()).equals(inpPwd))
					inpPwd = "";
				if (pwdPanelSelectCode == 1)
					System.exit(0);
			}
			tmpStr= JOptionPane.showInputDialog("Directory Path");
			if (tmpStr == null || tmpStr.trim().isEmpty())
				System.exit(0);
			File folder = new File(tmpStr.replace('\\','/'));		
			String[] options4Select = {"Encode","Decode","Quit"};
			final int optionCode = JOptionPane.showOptionDialog(null, "Select An Option","Operation",
					JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, 
					options4Select, options4Select[0]);	
			if (optionCode == 2)
				System.exit(0);
			File[] listOfFiles = folder.listFiles(new FileFilter() {
				@Override
			    public boolean accept(File pathname) {
					if (pathname.isFile()) {
						String fileName = getFileNameWithoutExtension(pathname.getName());
						String fileExt = getFileNameExtension(pathname.getName());
						if (optionCode == 0) {
							if (orgName.isEmpty()) {
					    		if (fileExt.contains("__")) {
					    			isAlphabetExtension = true;
					    			orgName = fileName;
					    			return true;
					    		} else if (isNumeric(fileExt)) {	
					    			isAlphabetExtension = false;
					    			orgName = fileName;
					    			return true;
					    		} else {
					    			return false;
					    		}
					    	} else if (fileName.equals(orgName)) {
						    	if (isAlphabetExtension)
						    		return fileExt.contains("__");
						    	else
						    		return isNumeric(fileExt);
					    	} else
					    		return false;
						} else {
							return true;
						}
					} else
						return false;		    	
			    }
			});
			if (listOfFiles == null || listOfFiles.length == 0) {
				JOptionPane.showMessageDialog(null, "Nothing To Do !");
				System.exit(0);
			} else if (isAlphabetExtension && listOfFiles.length > 26) {
				JOptionPane.showMessageDialog(null, "Please Check Number Of Files With Alphabetical Extension !");
				System.exit(0);
			}
			String folderPath = listOfFiles[0].getParent();
			Arrays.sort(listOfFiles);	
			int[] inpPwdNum = new int[inpPwd.length()];
			for (int i = 0; i < inpPwd.length(); ++i) {
				inpPwdNum[i] = anNamingCode.indexOf(inpPwd.charAt(i));
			}
			if (optionCode == 0) {				
				String backupFolderPath = folderPath + "/BackUp_" + new SimpleDateFormat("dd-MM-yyyy").format(new Date());
				File backupFolder = new File(backupFolderPath);
				if (!backupFolder.exists()) {
					if (!backupFolder.mkdir()) {
						JOptionPane.showMessageDialog(null, "Failed To Create Backup Directory !", "Error", JOptionPane.ERROR_MESSAGE);
						System.exit(0);
					}
					try {
						for(File aFile : listOfFiles) {					   
								Files.copy(aFile.toPath(),
											(new File(backupFolderPath + "/" + aFile.getName())).toPath(),
											StandardCopyOption.REPLACE_EXISTING);
						}
					} catch (IOException e) {
						e.printStackTrace();
						deleteDirectory(backupFolder);
						JOptionPane.showMessageDialog(null, "Failed To Create Backup Files !", "Error", JOptionPane.ERROR_MESSAGE);
						System.exit(0);
					}
				}
				String tmpOutStr = "";
				if (isAlphabetExtension)
					tmpOutStr = "a" + anNum2Char(listOfFiles.length);
				else {
					int[] fileNum = posDec2Base(listOfFiles.length,anCodeSum,2);
					tmpOutStr = "0" + anNum2Char(fileNum[0]) + anNum2Char(fileNum[1]);
				}			
				tmpStr = "";
				String tmpStr1 = "";
				String[] listOfFileNames = new String[listOfFiles.length];
				for (int i = 0; i < listOfFiles.length; i++) {
					tmpStr1 = anGenFileName();
					if (get1stIndStrArr(listOfFileNames,tmpStr1) == -1) {
						listOfFileNames[i] = tmpStr1;
						if (!listOfFiles[i].renameTo(new File(folderPath + "/" + tmpStr1))) {
							JOptionPane.showMessageDialog(null, "Failed To Modify File Names !", "Error", JOptionPane.ERROR_MESSAGE);
							System.exit(0);
						}
					tmpStr += anNum2Char(tmpStr1.length()) + tmpStr1;
					} else {
						--i;
						continue;
					}
				}
				tmpStr += orgName + inpPwd;
				tmpOutStr += anEncode(tmpStr);
				tmpStr = tmpOutStr;
				tmpOutStr = "";
				int inpPwdLen = inpPwd.length();
				for (int i = 0; i < tmpStr.length(); ++i) {
					tmpOutStr += anElmCycShift(tmpStr.charAt(i),inpPwdNum[i % inpPwdLen]);
				}
				try (PrintWriter dumper = new PrintWriter(folderPath + "/crypto.info")) {
					dumper.println(tmpOutStr);
				} catch (FileNotFoundException e) {				
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Failed To Gen Info !", "Error", JOptionPane.ERROR_MESSAGE);
					System.exit(0);
				}
			} else {
				String outputFolderPath = folderPath + "/Output_" + new SimpleDateFormat("dd-MM-yyyy").format(new Date());
				File outputFolder = new File(outputFolderPath);
				if (outputFolder.exists() && !deleteDirectory(outputFolder)) {
					
					JOptionPane.showMessageDialog(null, "Failed To Modify Output Directory !", "Error", JOptionPane.ERROR_MESSAGE);
					System.exit(0);
				} 
				if (!outputFolder.mkdir()) {
					JOptionPane.showMessageDialog(null, "Failed To Create Output Directory !", "Error", JOptionPane.ERROR_MESSAGE);
					System.exit(0);
				}
				String cryptoInfoStr = "";
				try {
					BufferedReader cryptoInfo=new BufferedReader(new FileReader(folderPath + "/crypto.info"));
					while((tmpStr=cryptoInfo.readLine())!=null) {
						cryptoInfoStr += tmpStr;
					}
					cryptoInfo.close();
				} catch (IOException e) {
					cryptoInfoStr = JOptionPane.showInputDialog("Code");
				}
				tmpStr = cryptoInfoStr.trim().replace(' ','\0').replace('\r','\0').replace('\n','\0');
				cryptoInfoStr = "";
				tmpInt = inpPwd.length();
				for (int i = 0; i < tmpStr.length(); ++i) {
					cryptoInfoStr += anElmCycShift(tmpStr.charAt(i),-inpPwdNum[i % tmpInt]);
				}
				char tmpChr = cryptoInfoStr.charAt(0);		
				if (tmpChr == '0') {
					isAlphabetExtension = false;
					tmpInt = anChar2Num(cryptoInfoStr.charAt(1));
				} else if (tmpChr == 'a') {
					isAlphabetExtension = true;
					tmpInt = anChar2Num(cryptoInfoStr.charAt(1)) + anChar2Num(cryptoInfoStr.charAt(2))*anCodeSum;
				} else {
					JOptionPane.showMessageDialog(null, "Failed To Decode !", "Error", JOptionPane.ERROR_MESSAGE);
					System.exit(0);
				}
				final int fileNum = tmpInt;
				if (isAlphabetExtension) {
					tmpStr = anDecode(cryptoInfoStr.substring(2));
				} else {
					tmpStr = anDecode(cryptoInfoStr.substring(3));
				}
				tmpInt = tmpStr.length()-inpPwd.length();
				if (!tmpStr.substring(tmpInt).equals(inpPwd)) {
					JOptionPane.showMessageDialog(null, "Failed To Decode !", "Error", JOptionPane.ERROR_MESSAGE);
					System.exit(0);
				}
				tmpStr = tmpStr.substring(0,tmpInt);
				String[] listOfFileNames = new String[fileNum];
				for (int i = 0; i < fileNum; ++i) {
					tmpInt = anChar2Num(tmpStr.charAt(0)) + 1;
					listOfFileNames[i] = tmpStr.substring(1, tmpInt);
					tmpStr = tmpStr.substring(tmpInt);
				}
				final String finalFileName = tmpStr;
				try {
					for (int i = 0; i < fileNum; ++i) {
						Files.copy(new File(folderPath + "/" + listOfFileNames[i]).toPath(),
								(new File(outputFolderPath + "/" + finalFileName + recoverFilePartExtension(isAlphabetExtension,i,3))).toPath(),
								StandardCopyOption.REPLACE_EXISTING);
					}
				} catch (IOException e) {
					e.printStackTrace();
					outputFolder.deleteOnExit();
					JOptionPane.showMessageDialog(null, "Failed To Recover Files !", "Error", JOptionPane.ERROR_MESSAGE);
					System.exit(0);
				}
			}
			JOptionPane.showMessageDialog(null, "Successfull !");
		} 
	}
	
	public static String getFileNameExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int extensionPos = filename.lastIndexOf('.');
        if (extensionPos == -1)
        	return "";
        int lastUnixPos = filename.lastIndexOf('/');
        int lastWindowsPos = filename.lastIndexOf('\\');
        int lastSeparator = Math.max(lastUnixPos, lastWindowsPos);

        int index = lastSeparator > extensionPos ? -1 : extensionPos;
        if (index == -1) {
            return "";
        } else {
            return filename.substring(index + 1);
        }
    }
	
	public static String getFileNameWithoutExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int extensionPos = filename.lastIndexOf('.');
        int lastUnixPos = filename.lastIndexOf('/');
        int lastWindowsPos = filename.lastIndexOf('\\');
        int lastSeparator = Math.max(lastUnixPos, lastWindowsPos);
        
        if (lastSeparator < (extensionPos - 1)) {
        	return filename.substring(lastSeparator + 1, extensionPos);
        } else if (lastSeparator < (filename.length() - 1)) {
        	return filename.substring(lastSeparator + 1);
        } else
        	return "";
    }
	
	public static boolean isNumeric(String strNum) {
	    try {
	        Double.parseDouble(strNum);
	    } catch (NumberFormatException | NullPointerException nfe) {
	        return false;
	    }
	    return true;
	}
	
	public static final String anCode = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final int anCodeSum = anCode.length();
	
	public static int anChar2Num(char aChar) {
		return anCode.indexOf(aChar);
	}
	
	public static char anNum2Char(int aNum) {
		return anCode.charAt(aNum);
	}
	
	public static int[] posDec2Base(int aNum, int aBase, int numSize) {
		int[] coeffs = new int[anCodeSum];
		int tmpCoeff = aNum;
		int i = 0;
		while (tmpCoeff >= aBase) {
			coeffs[i++] = tmpCoeff % aBase;
			tmpCoeff /= aBase;
		}
		coeffs[i++] = tmpCoeff;
		if (numSize <= i)
			return Arrays.copyOfRange(coeffs, 0, i);
		else {
			int[] tmpOut = new int[numSize];
			System.arraycopy(coeffs, 0, tmpOut, 0, i);
			return tmpOut;
		}			
	}
	
	public static String anEncode(String inpStr) {
		if (inpStr == null || inpStr.isEmpty())
			return "";
		String tmpOut = "";
		int[] inpAsciiCode = new int[inpStr.length()];
		for (int i = 0; i < inpStr.length(); ++i) {
			inpAsciiCode[i] = (int) inpStr.charAt(i);
		}		
		int[] maxAsciiCode = getMaxIntArr(inpAsciiCode);
		if (maxAsciiCode[1] != -1) {	
			int unitLength = (int) (Math.log10(maxAsciiCode[0]>1?maxAsciiCode[0]:1)/Math.log10(anCodeSum)) + 1;
			tmpOut += anNum2Char(unitLength);
			int[] tmpChar;
			for (int i = 0; i < inpAsciiCode.length; ++i) {
				tmpChar = posDec2Base(inpAsciiCode[i],anCodeSum,unitLength);
				for (int j = 0; j < unitLength; ++j) {
					tmpOut += anNum2Char(tmpChar[j]);
				}
			}
		}		
		return tmpOut;
	}
	
	public static String anDecode(String inpStr) {
		if (inpStr == null || inpStr.isEmpty())
			return "";
		String tmpOut = "";
		final int unitLength = anChar2Num(inpStr.charAt(0));
		int tmpInt = 0;
		for (int i = 1; i < inpStr.length();) {
			tmpInt = 0;
			for (int j = 0; j < unitLength; ++j) {				
				tmpInt += anChar2Num(inpStr.charAt(i++)) * Math.pow(anCodeSum, j);
			}
			tmpOut += (char) tmpInt;
		}
		return tmpOut;
	}
	
	public static int[] getMaxIntArr(int[] intArr) {
		int[] intMaxAndInd = new int[] {0,-1};		
		for (int i = 0; i < intArr.length; ++i) {
			if (intArr[i] > intMaxAndInd[0]) {
				intMaxAndInd[0] = intArr[i];
				intMaxAndInd[1] = i;
			}
		}
		return intMaxAndInd;
	}
	
	public static int get1stIndStrArr(String[] strArr, String aStr) {
		for (int i = 0; i < strArr.length; ++i) {
			if (strArr[i] == null)
				break;
			if (strArr[i].equals(aStr))
				return i;
		}
		return -1;
	}
	
	public static final String anNamingCode = "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿŒŽƒŠABCDœžŸEFGHIJKLMNOPQRSTUVWXYZ š0123456789ª_º.—ˆ-¼½¾–¹²³abcdefghijklmnopqrstuvwxyz";
	public static final int anNamingCodeSum = anNamingCode.length();
	
	public static String anGenFileName() {
		String tmpOut = "";
		final int maxNum = anNamingCodeSum - 1;
		char tmpChr = '\0';
		int nameLength = (int) (Math.random() * 10) + 1;
		for (int i = 1; i <= nameLength; ++i) {						
			tmpChr = anNamingCode.charAt((int) (Math.random()*maxNum));
			if (i == 1 || i == nameLength) {
				if (tmpChr == ' ') {
					--i;
					continue;
				}
			}
			tmpOut += tmpChr;
		}
		return tmpOut;
	}
	
	public static char anElmCycShift(char anChr, int aNum) {
		return anNum2Char((anChar2Num(anChr) + (aNum % anCodeSum) + anCodeSum) % anCodeSum);
	}
	
	public static String recoverFilePartExtension(boolean isAlphabetExtension, int partNum, int extLen) {
		if (isAlphabetExtension) {
			int realNumSize = partNum >= 1?(int) (Math.log10(partNum)/Math.log10(26)) + 1:1;
			int[] partNumInAlphaBase = posDec2Base(partNum,26,extLen);
			String tmpOut = "";
			for (int i = extLen - 1; i >= realNumSize; --i) {
				tmpOut += "_";
			}
			tmpOut += (char) (partNumInAlphaBase[realNumSize-1]-(realNumSize>1?1:0) + (int) 'a');
			for (int i = realNumSize - 2; i >= 0; --i) {
				tmpOut += (char) (partNumInAlphaBase[i] + (int) 'a');
			}
			return "." + tmpOut;
		} else {
			String tmpOut = Integer.toString(partNum + 1);
			for (int i = tmpOut.length(); i < extLen; ++i) {
				tmpOut = "0" + tmpOut;
			}
			return "." + tmpOut;
		}
	}
	
	public static boolean deleteDirectory(File directoryToBeDeleted) {
	    File[] allContents = directoryToBeDeleted.listFiles();
	    if (allContents != null) {
	        for (File file : allContents) {
	            deleteDirectory(file);
	        }
	    }
	    return directoryToBeDeleted.delete();
	}

}
