package prglabclient;

import java.util.*;

/**
 *  ласс-синглетон дл€ выбора €зыковых параметров и получени€ сообщений дл€ выбранного €зыка
 */
public class LocaleData{

	static ResourceBundle m_localeBundle;
	static LocaleData     m_instance;

	public static final String defLang = "en";
	public static final String defCountry = "US";
//	public static final String defLang = "ru";
//	public static final String defCountry = "RU";

	/**
	 * «акрытый конструктор класса
	 * @param lang код €зыка (например, ru или en)
	 * @param country код страны (например, RU или EN)
	 */
	private LocaleData( String lang, String country ){
		Locale loc = new Locale( lang, country );
		m_localeBundle = ResourceBundle.getBundle( "TextBundle", loc );
	}

	/**
	 * ћетод дл€ получени€ инкапсулированных €зыковых настроек
	 */
	public static LocaleData getInstance( String lang, String country ){
		if( m_instance == null )
			m_instance = new LocaleData( lang, country );
		return m_instance;
	}

	/**
	 * ћетод дл€ получени€ ранее инициализированного экземпл€ра класса (использует пол€ defLang и defCountry, если класс не был инициализирован)
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
	 * ѕолучение строки на выбранном при инициализации €зыке
	 */
	public String getString( String key ){
		return m_localeBundle.getString(key);
	}
}