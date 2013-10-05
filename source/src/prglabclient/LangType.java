package prglabclient;

/**
 * Класс для описания языка программирования
 */
public class LangType{
	/**
	 * Название языка (отображается в списках)
	 */
    public String name;
	/**
	 * Краткое название языка (передается на сервер)
	 */
    public String val;
	/**
	 * Признак чувствительности к регистру символов
	 */
    public boolean bCaseSens = false;
	/**
	 * Текст простой демонстрационной программы
	 */
    public String simpleProgram = "";

	/**
	 * Конструктор
	 */
    public LangType( String name, String val, boolean bCaseSens, String simpleProgram ){
        this.name = name;
        this.val = val;
        this.bCaseSens = bCaseSens;
        this.simpleProgram = simpleProgram;
    }
	/**
	 * Перегруженный метод, возвращает поле name
	 */
    public String toString(){ 
        return name;
    }
}
