package prglabclient;

import java.util.*;

/**
 * �����-��������� ��� ������ �������� ���������� � ��������� ��������� ��� ���������� �����
 */
public class LocaleData{

	static ResourceBundle m_localeBundle;
	static LocaleData     m_instance;

	public static final String defLang = "en";
	public static final String defCountry = "US";
//	public static final String defLang = "ru";
//	public static final String defCountry = "RU";

	/**
	 * �������� ����������� ������
	 * @param lang ��� ����� (��������, ru ��� en)
	 * @param country ��� ������ (��������, RU ��� EN)
	 */
	private LocaleData( String lang, String country ){
		Locale loc = new Locale( lang, country );
		m_localeBundle = ResourceBundle.getBundle( "TextBundle", loc );
	}

	/**
	 * ����� ��� ��������� ����������������� �������� ��������
	 */
	public static LocaleData getInstance( String lang, String country ){
		if( m_instance == null )
			m_instance = new LocaleData( lang, country );
		return m_instance;
	}

	/**
	 * ����� ��� ��������� ����� ������������������� ���������� ������ (���������� ���� defLang � defCountry, ���� ����� �� ��� ���������������)
	 */
	public static LocaleData getInstance(){
		if( m_instance == null ){
			m_instance = new LocaleData( defLang, defCountry );
//			Locale def = Locale.getDefault();
//System.out.println( "def locale: " + def.getLanguage() + "_" + def.getCountry() );
//			m_instance = new LocaleData( def.getLanguage(), def.getCountry() );
		}
		return m_instance;
	}

	/**
	 * ��������� ������ �� ��������� ��� ������������� �����
	 */
	public String getString( String key ){
		return m_localeBundle.getString(key);
	}
}