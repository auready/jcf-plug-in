package jcf.gen.eclipse.core.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

/**
 * <p>내용          : 문자열 처리에 필요한 Util Class</p>
 * @author 권순정
 * @version 1.0
 */
public class StrUtils {

	/**
	 * String Array 를 Vector 형으로 변환
	 * @param array String[]
	 * @return Vector
	 */
	public static Vector<String> arrayToVector(String[] array) {
		if (array == null) {
			return null;
		}
		Vector<String> vec = new Vector<String>();
		for (int i = 0; i < array.length; i++) {
			vec.add(array[i]);
		}
		return vec;
	}

	/**
	 * Vector 를 String Array 로 변환
	 * @param vec Vector
	 * @return String[]
	 */
	public static String[] vectorToArray(Vector<String> vec) {
		if (vec == null) {
			return null;
		}
		String[] array = new String[vec.size()];
		for (int i = 0; i < vec.size(); i++) {
			array[i] = (String) vec.get(i);
		}
		return array;
	}

	/**
	 * ['] 문자를 DB에 넣기 위하여 [''] 로 변환 처리
	 * @param original
	 * @return
	 */
	public static String processQuotation(String original) {
		if (original == null) {
			return null;
		} else {
			return replaceAll(original, "'", "''");
		}
	}

	/**
	 * 특수 문자를 HTML 표시 가능 특수 문자 형식으로 변환
	 * @param original
	 * @return
	 */
	public static String processHtmlQuotation(String original) {
		if (original == null) {
			return null;
		}
		StringBuffer retVal = new StringBuffer();
		Character sq = new Character('\'');
		Character dq = new Character('"');
		for (int i = 0; i < original.length(); i++) {
			char c = original.charAt(i);
			Character cObj = new Character(c);
			if (cObj.compareTo(sq) == 0) {
				Character reverse = new Character('\\');
				retVal.append(reverse);
				retVal.append(sq);
			} else if (cObj.compareTo(dq) == 0) {
				Character reverse = new Character('\\');
				retVal.append(reverse);
				retVal.append(dq);
			} else {
				retVal.append(cObj);
			}
		}
		return retVal.toString();
	}

	/**
	 * 입력 받은 String 에서 find 문자를 replacement 문자로 치환하기
	 * @param original 변경대상 String
	 * @param find 변경대상 문자
	 * @param replacement 치환 문자
	 * @return 치환 완료 되어진 String
	 */
	public static String replaceAll(String original, String find,
			String replacement) {
		StringBuffer buffer = new StringBuffer(original);
		return replaceAll(buffer, find, replacement).toString();
	}

	/**
	 * 입력 받은 StringBuffer 에서 find 문자를 replacement 문자로 치환하기
	 * @param buffer 변경대상 StringBuffer
	 * @param find 변경대상 문자
	 * @param replacement 치환 문자
	 * @return 치환 완료 되어 진 StringBuffer
	 */
	public static StringBuffer replaceAll(StringBuffer buffer, String find,
			String replacement) {
		int bufidx = buffer.length() - 1;
		int offset = find.length();

		while (bufidx > -1) {
			int findidx = offset - 1;
			while (findidx > -1) {
				if (bufidx == -1) {
					return buffer;
				}

				if (buffer.charAt(bufidx) == find.charAt(findidx)) {
					findidx--; //Look for next char
					bufidx--;
				} else {
					findidx = offset - 1; //Start looking again
					bufidx--;

					if (bufidx == -1) {
						//Done
						return buffer;
					}
					continue;
				}
			}
			buffer.replace(bufidx + 1, bufidx + 1 + offset, replacement);
		}
		return buffer;
	}

	/**
	 * 입력 받은 String 배열을 separators 문자로 구분 하여 하나의 String 으로 변환
	 * @param array
	 * @param separators 구분 문자[ Default Value : "," ]
	 * @return
	 */
	public static final String arrayToString(String[] array, String separators) {
		StringBuffer sb = new StringBuffer("");
		String empty = "";

		if (array == null)     { return empty; 	}
		if (separators == null) { separators = ","; }

		for (int ix = 0; ix < array.length; ix++) {
			if ((array[ix] != null) && !array[ix].equals("")) {
				sb.append(array[ix] + separators);
			}
		}
		String str = sb.toString();
		if (!str.equals("")) {
			str = str.substring(0, (str.length() - separators.length()));
		}
		return str;
	}

	/**
	 * 입력 받은 String 배열을 하나의 String 으로 변환
	 * @param array
	 * @return
	 */
	public static final String arrayToString(String[] array) {
		StringBuffer sb = new StringBuffer("");
		String empty = "";

		if (array == null)     { return empty; 	}

		for (int ix = 0; ix < array.length; ix++) {
			if ((array[ix] != null) && !array[ix].equals("")) {
				sb.append(array[ix]);
			}
		}
		String str = sb.toString();
		if (!str.equals("")) {
			str = str.substring(0, str.length());
		}
		return str;
	}

	/**
	 * 입력 받은 String 을 separators 문자로 구분 하여 String Array 로 변환
	 * @param str
	 * @param separators 구분 문자[ Default Value : "," ]
	 * @return
	 */
	public static final String[] stringToArray(String str, String separators) {
		StringTokenizer tokenizer;
		String[] array = null;
		int count = 0;

		if (str == null)        { return array; 		}
		if (separators == null) { separators = ","; 	}

		tokenizer = new StringTokenizer(str, separators);
		if ((count = tokenizer.countTokens()) <= 0) {
			return array;
		}
		array = new String[count];
		int ix = 0;
		while (tokenizer.hasMoreTokens()) {
			array[ix] = tokenizer.nextToken();
			ix++;
		}
		return array;
	}

	/**
	 * 입력 받은 String 에서 removeChars 를 제거한다.
	 * @param data
	 * @param removeChars
	 * @return
	 */
	public static String removeChars(String data, String removeChars) {
		StringBuffer out = new StringBuffer();
		StringTokenizer st = new StringTokenizer(data, removeChars);
		while (st.hasMoreTokens()) {
			String element = (String) st.nextElement();
			out.append(element);
		}
		return out.toString();
	}

	/**
	 * 입력값이 null 이 아니면 Trim 처리하여 반환한다. null 이면 null 반환
	 * @param str
	 * @return
	 */
	public static String trim(String str) {
		return (str == null) ? null : str.trim();
	}

	/**
	 * 입력값이 null 이 아니면 Right Trim 처리하여 반환한다. null 이면 null 반환
	 * @param str
	 * @return
	 */
	public static String rtrim(String str) {
		if(str == null) return null;
		int i = str.length()-1;
		for(; i >= 0; i--) {
			if(str.charAt(i) != ' ') break;
		}
		return str.substring(0, i+1);
	}

	/**
	 * 입력값이 null 이 아니면 Left Trim 처리하여 반환한다. null 이면 null 반환
	 * @param str
	 * @return
	 */
	public static String ltrim(String str) {
		if(str == null) return null;
		int i = 0;
		for(; i < str.length(); i++) {
			if(str.charAt(i) != ' ') break;
		}
		return str.substring(i, str.length());
	}

	/**
	 * 입력한 문자[strPutData]로 지정된 길이[intCompleteLength] 만큼 Right Padding 처리 한다.
	 * @param strData
	 * @param intCompleteLength
	 * @param strPutData
	 * @return
	 * @throws Exception
	 */
	public static String rpad(String strData, int intCompleteLength, String strPutData) throws Exception {
		StringBuffer stb = new StringBuffer(strData);
		int chck = 0;
		while(length(stb.toString())<intCompleteLength) {
			stb.append(strPutData);
			if(chck++ > 9999) throw new Exception("rpad Exception");
		}
		strData = stb.toString();
		return strData;
	}

	/**
	 * 입력한 문자[strPutData]로 지정된 길이[intCompleteLength] 만큼 Left Padding 처리 한다.
	 * @param strData
	 * @param intCompleteLength
	 * @param strPutData
	 * @return
	 * @throws Exception
	 */
	public static String lpad(String strData, int intCompleteLength, String strPutData) throws Exception {
		StringBuffer stb = new StringBuffer(strData);
		int chck = 0;
		while(length(stb.toString())<intCompleteLength) {
			stb.insert(0, strPutData);
			if(chck++ > 9999) throw new Exception("lpad Exception");
		}
		strData = stb.toString();
		return strData;
	}


	/**
	 * 입력값이 null 이면 초기값을 반환 한다.
	 * @param str
	 * @param initValue
	 * @return
	 */
	public static String nvl(String str, String initValue) {
		if( (str == null) || (str.trim().equals("")) || (str.trim().equalsIgnoreCase("NULL")) ) {
			return (initValue == null) ? "" : initValue;
		}
		return str;
	}

	/**
	 * 입력값이 null 이면 Empty String[""] 을 반환 한다.
	 * @param str
	 * @return
	 */
	public static String nvl(String str) {
		return nvl(str, null);
	}

	/**
	 * 입력값이 null 이면 "0" 을 반환 한다.
	 * @param str
	 * @return
	 */
	public static String nvz(String str) {
		return nvl(str, "0");
	}

	/**
	 * 입력 값이 null 이면 "" 를 반환 하고, null 이 아니면 trim 처리 하여 반환한다.
	 * @param str
	 * @return
	 */
	public static String nts(String str) {
		return trim(nvl(str));
	}

	/**
	 * 입력 값이 null 이면 "0" 를 반환 하고, null 이 아니면 trim 처리 하여 반환한다.
	 * @param str
	 * @return
	 */
	public static String ntz(String str) {
		return trim(nvl(str, "0"));
	}

	/**
	 * String 문자열의 Byte 길이 반환
	 * @param str
	 * @return
	 */
	public static int length(String str) {
		if(str == null || "".equals(str)) return 0;
		return str.getBytes().length;
	}

	/**
	 * byte 크기 만큼  문자열 자르기
	 * @param str 입력 문자열
	 * @param size byte size
	 * @return
	 * @see #substr(String, int, int)
	 */
	public static String substr(String str, int size) {
		return substr(str, 0, size);
	}

	/**
	 * byte 기준 문자열 자르기
	 * <p>자르기 시작 지점 : 시작 byte 위치가 2bytes 문자일 경우 그 전 byte 부터 잘라 문자가 깨지지 않게 한다.
	 * <p>자르기 끝내기 지점 : 자르기 시작 지점 부터 endbyte - startbyte 길이 만큼 자른다.
	 *                   해당 위치가 2bytes 문자일 경우 그 문자는 제외 시킨다.
	 * <pre><code>
	 *     substr("가나567890가나다", 0,10), is("가나567890")  // 0 byte 부터 10byte 길이 만큼 자른다.
	 *     substr("가나567890가나다", 0,11), is("가나567890")  // 0 byte 부터 11byte 길이 만큼 자른다. [11 byte 문자가 2bytes 문자 '가' 이므로 제외시킨다. ]
	 *     substr("가나567890가나다", 3,14), is("나567890가")  // 3 byte 부터 11byte 길이 만큼 자른다. [3 byte 문자가 2bytes 문자 '나' 이므로 2 byte 부터 자른다.]
	 *     substr("가나567890가나다", 2,14), is("나567890가나")// 2 byte 부터 12byte 길이 만큼 자른다.
	 *     substr("가나다",2,4), is("나")                      // 2 byte 부터  2byte 길이 만큼 자른다.
	 *  </code></pre>
	 * @param str 입력 문자열
	 * @param sindex 시작 byte
	 * @param eindex 종료 byte
	 * @return
	 * @see #substr(String, int)
	 */
	public static String substr(String str, int sindex, int eindex) {
		String strpos = str.substring(0, sindex);
		int spos = 0;
		while(true)
		{
			if(length(strpos) <= sindex) {
				spos = strpos.length();
				break;
			}
			else strpos = strpos.substring(0, strpos.length()-1);
		}
		String endpos = str.substring(spos, str.length());
		while(true)
		{
			if(length(endpos) <= eindex - sindex) {
				break;
			}
			else endpos = endpos.substring(0, endpos.length()-1);
		}
		return endpos;
	}

	/**
	 * 입력 문자열에서 숫자만 뽑아낸다.
	 * @param str
	 * @return
	 * <pre><code>
	 * getNumber("가나123다라3424$%#$%#$ㅇ232"), is("1233424232")
	 * </code></pre>
	 */
	public static String getNumber(String str) {
		str = StrUtils.nvl(str).replaceAll("[^\\d]", "");
		return str;
	}

	/**
	 * 입력 받은 숫자 문자열에 천단위 표시를 추가한다.
	 * @param str
	 * @return
	 */
	public static String convNumToPattern(String str) {
		BigDecimal bd = new BigDecimal(str);
		return convNumToPattern(bd);
	}

	/**
	 * 입력 받은 숫자 문자열에 천단위 표시를 추가한다.
	 * @param bd
	 * @return
	 */
	public static String convNumToPattern(BigDecimal bd) {
		DecimalFormat format = new DecimalFormat("###,###,###,###,###,###,###,###,###,###,###,###,###.#####");
		return format.format(bd);
	}

	/**
	 * 입력 받은 숫자 문자열 올림 처리
	 * @param str
	 * @param scale 소수점 자리수
	 * @return
	 */
	public static String roundUp(String str, int scale) {
		BigDecimal bd = new BigDecimal(str);
		BigDecimal div = new BigDecimal("1");
		double rslt = bd.divide(div, scale, BigDecimal.ROUND_UP).doubleValue();
		return String.valueOf(rslt);
	}

	/**
	 * 입력 받은 숫자 문자열 내림 처리
	 * @param str
	 * @param scale 소수점 자리수
	 * @return
	 */
	public static String roundDown(String str, int scale) {
		BigDecimal bd = new BigDecimal(str);
		BigDecimal div = new BigDecimal("1");
		double rslt = bd.divide(div, scale, BigDecimal.ROUND_DOWN).doubleValue();
		return String.valueOf(rslt);
	}

	/**
	 * 입력 받은 숫자 문자열 반올림 처리
	 * @param str
	 * @param scale 소수점 자리수
	 * @return
	 */
	public static String roundHalfUp(String str, int scale) {
		BigDecimal bd = new BigDecimal(str);
		BigDecimal div = new BigDecimal("1");
		double rslt = bd.divide(div, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
		return String.valueOf(rslt);
	}

	/**
	 * 전각 -> 반각
	 * @param fileCntn
	 * @return 반각, null 또는"" 입력시 "" 리턴
	 */
	/*
	public static String replaceChar(String fileCntn) {
		if(fileCntn == null || fileCntn.length() < 1) {
			return "";
		}
		String retVal = fileCntn.replace('０', '0');
		retVal = retVal.replace('１', '1');
		retVal = retVal.replace('２', '2');
		retVal = retVal.replace('３', '3');
		retVal = retVal.replace('４', '4');
		retVal = retVal.replace('５', '5');
		retVal = retVal.replace('６', '6');
		retVal = retVal.replace('７', '7');
		retVal = retVal.replace('８', '8');
		retVal = retVal.replace('９', '9');
		retVal = retVal.replace('Ａ', 'A');
		retVal = retVal.replace('Ｂ', 'B');
		retVal = retVal.replace('Ｃ', 'C');
		retVal = retVal.replace('Ｄ', 'D');
		retVal = retVal.replace('Ｅ', 'E');
		retVal = retVal.replace('Ｆ', 'F');
		retVal = retVal.replace('Ｇ', 'G');
		retVal = retVal.replace('Ｈ', 'H');
		retVal = retVal.replace('Ｉ', 'I');
		retVal = retVal.replace('Ｊ', 'J');
		retVal = retVal.replace('Ｋ', 'K');
		retVal = retVal.replace('Ｌ', 'L');
		retVal = retVal.replace('Ｍ', 'M');
		retVal = retVal.replace('Ｎ', 'N');
		retVal = retVal.replace('Ｏ', 'O');
		retVal = retVal.replace('Ｐ', 'P');
		retVal = retVal.replace('Ｑ', 'Q');
		retVal = retVal.replace('Ｒ', 'R');
		retVal = retVal.replace('Ｓ', 'S');
		retVal = retVal.replace('Ｔ', 'T');
		retVal = retVal.replace('Ｕ', 'U');
		retVal = retVal.replace('Ｖ', 'V');
		retVal = retVal.replace('Ｗ', 'W');
		retVal = retVal.replace('Ｘ', 'X');
		retVal = retVal.replace('Ｙ', 'Y');
		retVal = retVal.replace('Ｚ', 'Z');
		retVal = retVal.replace('ａ', 'a');
		retVal = retVal.replace('ｂ', 'b');
		retVal = retVal.replace('ｃ', 'c');
		retVal = retVal.replace('ｄ', 'd');
		retVal = retVal.replace('ｅ', 'e');
		retVal = retVal.replace('ｆ', 'f');
		retVal = retVal.replace('ｇ', 'g');
		retVal = retVal.replace('ｈ', 'h');
		retVal = retVal.replace('ｉ', 'i');
		retVal = retVal.replace('ｊ', 'j');
		retVal = retVal.replace('ｋ', 'k');
		retVal = retVal.replace('ｌ', 'l');
		retVal = retVal.replace('ｍ', 'm');
		retVal = retVal.replace('ｎ', 'n');
		retVal = retVal.replace('ｏ', 'o');
		retVal = retVal.replace('ｐ', 'p');
		retVal = retVal.replace('ｑ', 'q');
		retVal = retVal.replace('ｒ', 'r');
		retVal = retVal.replace('ｓ', 's');
		retVal = retVal.replace('ｔ', 't');
		retVal = retVal.replace('ｕ', 'u');
		retVal = retVal.replace('ｖ', 'v');
		retVal = retVal.replace('ｗ', 'w');
		retVal = retVal.replace('ｘ', 'x');
		retVal = retVal.replace('ｙ', 'y');
		retVal = retVal.replace('ｚ', 'z');
		retVal = retVal.replace('～', '~');
		retVal = retVal.replace('｀', '`');
		retVal = retVal.replace('！', '!');
		retVal = retVal.replace('＠', '@');
		retVal = retVal.replace('＃', '#');
		retVal = retVal.replace('＄', '$');
		retVal = retVal.replace('％', '%');
		retVal = retVal.replace('＾', '^');
		retVal = retVal.replace('＆', '&');
		retVal = retVal.replace('＊', '*');
		retVal = retVal.replace('（', '(');
		retVal = retVal.replace('）', ')');
		retVal = retVal.replace('－', '-');
		retVal = retVal.replace('＿', '_');
		retVal = retVal.replace('＝', '=');
		retVal = retVal.replace('＋', '+');
		retVal = retVal.replace('［', '[');
		retVal = retVal.replace('］', ']');
		retVal = retVal.replace('｛', '{');
		retVal = retVal.replace('｝', '}');
		retVal = retVal.replace('＇', '\'');
		retVal = retVal.replace('＂', '"');
		retVal = retVal.replace('；', ';');
		retVal = retVal.replace('：', ':');
		retVal = retVal.replace('，', ',');
		retVal = retVal.replace('．', '.');
		retVal = retVal.replace('／', '/');
		retVal = retVal.replace('＜', '<');
		retVal = retVal.replace('＞', '>');
		retVal = retVal.replace('？', '?');
		retVal = retVal.replace('￦', '\\');
		retVal = retVal.replace('　', ' ');
		retVal = retVal.replace('｜', '|');

		return retVal;
	}
	*/
	/**
	 * 반각 -> 전각
	 * @param fileCntn
	 * @return 전각, null 또는"" 입력시 "" 리턴
	 */
	/*
	public static String replaceCharQuard(String fileCntn) {
		if(fileCntn == null || fileCntn.length() < 1) {
			return "";
		}

		String retVal = fileCntn.replace ('0','０');
		retVal = retVal.replace   ('1','１');
		retVal = retVal.replace   ('2','２');
		retVal = retVal.replace   ('3','３');
		retVal = retVal.replace   ('4','４');
		retVal = retVal.replace   ('5','５');
		retVal = retVal.replace   ('6','６');
		retVal = retVal.replace   ('7','７');
		retVal = retVal.replace   ('8','８');
		retVal = retVal.replace   ('9','９');
		retVal = retVal.replace   ('A','Ａ');
		retVal = retVal.replace   ('B','Ｂ');
		retVal = retVal.replace   ('C','Ｃ');
		retVal = retVal.replace   ('D','Ｄ');
		retVal = retVal.replace   ('E','Ｅ');
		retVal = retVal.replace   ('F','Ｆ');
		retVal = retVal.replace   ('G','Ｇ');
		retVal = retVal.replace   ('H','Ｈ');
		retVal = retVal.replace   ('I','Ｉ');
		retVal = retVal.replace   ('J','Ｊ');
		retVal = retVal.replace   ('K','Ｋ');
		retVal = retVal.replace   ('L','Ｌ');
		retVal = retVal.replace   ('M','Ｍ');
		retVal = retVal.replace   ('N','Ｎ');
		retVal = retVal.replace   ('O','Ｏ');
		retVal = retVal.replace   ('P','Ｐ');
		retVal = retVal.replace   ('Q','Ｑ');
		retVal = retVal.replace   ('R','Ｒ');
		retVal = retVal.replace   ('S','Ｓ');
		retVal = retVal.replace   ('T','Ｔ');
		retVal = retVal.replace   ('U','Ｕ');
		retVal = retVal.replace   ('V','Ｖ');
		retVal = retVal.replace   ('W','Ｗ');
		retVal = retVal.replace   ('X','Ｘ');
		retVal = retVal.replace   ('Y','Ｙ');
		retVal = retVal.replace   ('Z','Ｚ');
		retVal = retVal.replace   ('a','ａ');
		retVal = retVal.replace   ('b','ｂ');
		retVal = retVal.replace   ('c','ｃ');
		retVal = retVal.replace   ('d','ｄ');
		retVal = retVal.replace   ('e','ｅ');
		retVal = retVal.replace   ('f','ｆ');
		retVal = retVal.replace   ('g','ｇ');
		retVal = retVal.replace   ('h','ｈ');
		retVal = retVal.replace   ('i','ｉ');
		retVal = retVal.replace   ('j','ｊ');
		retVal = retVal.replace   ('k','ｋ');
		retVal = retVal.replace   ('l','ｌ');
		retVal = retVal.replace   ('m','ｍ');
		retVal = retVal.replace   ('n','ｎ');
		retVal = retVal.replace   ('o','ｏ');
		retVal = retVal.replace   ('p','ｐ');
		retVal = retVal.replace   ('q','ｑ');
		retVal = retVal.replace   ('r','ｒ');
		retVal = retVal.replace   ('s','ｓ');
		retVal = retVal.replace   ('t','ｔ');
		retVal = retVal.replace   ('u','ｕ');
		retVal = retVal.replace   ('v','ｖ');
		retVal = retVal.replace   ('w','ｗ');
		retVal = retVal.replace   ('x','ｘ');
		retVal = retVal.replace   ('y','ｙ');
		retVal = retVal.replace   ('z','ｚ');
		retVal = retVal.replace   ('~','～');
		retVal = retVal.replace   ('`','｀');
		retVal = retVal.replace   ('!','！');
		retVal = retVal.replace   ('@','＠');
		retVal = retVal.replace   ('#','＃');
		retVal = retVal.replace   ('$','＄');
		retVal = retVal.replace   ('%','％');
		retVal = retVal.replace   ('^','＾');
		retVal = retVal.replace   ('&','＆');
		retVal = retVal.replace   ('*','＊');
		retVal = retVal.replace   ('(','（');
		retVal = retVal.replace   (')','）');
		retVal = retVal.replace   ('-','－');
		retVal = retVal.replace   ('_','＿');
		retVal = retVal.replace   ('=','＝');
		retVal = retVal.replace   ('+','＋');
		retVal = retVal.replace   ('[','［');
		retVal = retVal.replace   (']','］');
		retVal = retVal.replace   ('{','｛');
		retVal = retVal.replace   ('}','｝');
		retVal = retVal.replace   ('\'', '＇');
		retVal = retVal.replace   ('"','＂');
		retVal = retVal.replace   (';','；');
		retVal = retVal.replace   (':','：');
		retVal = retVal.replace   (',','，');
		retVal = retVal.replace   ('.','．');
		retVal = retVal.replace   ('/','／');
		retVal = retVal.replace   ('<','＜');
		retVal = retVal.replace   ('>','＞');
		retVal = retVal.replace   ('?','？');
		retVal = retVal.replace   ('\\','￦');
		retVal = retVal.replace   (' ','　');
		retVal = retVal.replace   ('|','｜');
		return retVal;
	}
	*/
	public static String camelCaseConverter(String columnName) {
        String newColumnName = null;

        if (columnName.indexOf("_") == -1)
            newColumnName = columnName.toLowerCase();
        else {
            StringBuffer sb = new StringBuffer();
            boolean isFirst = true;
            StringTokenizer tokenizer = new StringTokenizer(columnName, "_");
            while (tokenizer.hasMoreTokens()) {
                if (isFirst)
                    sb.append(tokenizer.nextToken().toLowerCase());
                else {
                    sb.append(StringUtils.capitalize(tokenizer.nextToken()
                            .toLowerCase()));
                }
                isFirst = false;
            }

            newColumnName = sb.toString();
        }
        return newColumnName;
    }

    public static String pascalCaseConverter(String columnName) {
    	String newColumnName = null;

        if (columnName.indexOf("_") == -1)
            newColumnName = columnName.toLowerCase();
        else {
            StringBuffer sb = new StringBuffer();
            StringTokenizer tokenizer = new StringTokenizer(columnName, "_");
            while (tokenizer.hasMoreTokens()) {
                    sb.append(StringUtils.capitalize(tokenizer.nextToken()
                            .toLowerCase()));
            }

            newColumnName = sb.toString();
        }

        return newColumnName;
    }
    
    public static int seperateFileFromPkg(String str) {
    	int lastIdx = str.lastIndexOf('.');
    	
    	for (int i = lastIdx - 1; i > 0; i--) {
    		if (str.charAt(i) == '.') {
    			return i;
    		}
    	}
    	
    	return lastIdx;
    }
}

