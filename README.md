# Reflection
Разработаны собственные фреймворки, использующие механизм отражения.

## Introduction
- Для написания программ использовалась версия Java 8.
- Отладка кода воспроизводилась на Intellij IDEA CE.
- Правила форматирования кода соответствуют общепринятым стандартам [Oracle](https://www.oracle.com/java/technologies/javase/codeconventions-namingconventions.html).
- Использованы зависимости и плагины для обеспечения корректной работы:
    - reflections
    - auto-service
    - maven-compiler-plugin
    - postgresql
    - HikariCP
    - maven-assembly-plugin

## Contents
1. [Exercise 00](#exercise-00)
2. [Exercise 01](#exercise-01)
3. [Exercise 02](#exercise-02)

### Exercise 00

- Программа расположена в директории: ex00;
- Корневая папка проекта: Reflection.

Реализован Maven проект, который взаимодействует с классами приложения. Созданы два класса (User и Car), каждый из которых имеет:
- частные поля (поддерживаемые типы: String, Integer, Double, Boolean, Long)
- публичные методы
- пустой конструктор
- конструктор с параметром
- метод toString()

В этой задаче нет необходимости реализовывать методы get/set.

Приложение работает следующим образом:
- Предоставляет информацию о классе в пакете классов.
- Разрешает пользователю создавать объекты указанного класса с определенными значениями полей.
- Отображает информацию о созданном объекте класса.
- Вызывает методы класса.

**Дополнительно**
- Если метод содержит более одного параметра, необходимо установить значения для каждого из них.
- Если метод имеет тип void, то строка с информацией о возвращаемом значении не отображается.
- В сеансе программы возможно взаимодействие только с одним классом: можно изменить одно поле его объекта и вызвать один метод.

**Пример работы программы:**

```
Classes:
  - User
  - Car
---------------------
Enter class name:
-> User
---------------------
fields:
	String firstName
	String lastName
	int height
methods:
	int grow(int)
---------------------
Let’s create an object.
firstName:
-> UserName
lastName:
-> UserSurname
height:
-> 185
Object created: User[firstName='UserName', lastName='UserSurname', height=185]
---------------------
Enter name of the field for changing:
-> firstName
Enter String value:
-> Name
Object updated: User[firstName='Name', lastName='UserSurname', height=185]
---------------------
Enter name of the method for call:
-> grow(int)
Enter int value:
-> 10
Method returned:
195
```

### Exercise 01

- Программа расположена в директории: ex01;
- Корневая папка проекта: Annotations.

Реализован класс HtmlProcessor (производный от AbstractProcessor), который обрабатывает классы со специальными аннотациями @HtmlForm и @Htmlnput и генерирует код формы HTML внутри папки target/classes после выполнения команды mvn clean compile. 

На основе класса UserForm создается файл «user_form.html» со следующим содержимым:

```HTML
<form action = "/users" method = "post">
	<input type = "text" name = "first_name" placeholder = "Enter First Name">
	<input type = "text" name = "last_name" placeholder = "Enter Last Name">
	<input type = "password" name = "password" placeholder = "Enter Password">
	<input type = "submit" value = "Send">
</form>
```

- Аннотации @HtmlForm и @HtmlInput доступны только во время компиляции.
- Для правильной обработки аннотаций используются специальные настройки maven-compiler-plugin и зависимость автосервиса от com.google.auto.service.

### Exercise 02

- Программа расположена в директории: ex02;
- Корневая папка проекта: ORM.

Реализована тривиальную версия структуры ORM. Концепция ORM позволяет автоматически сопоставлять реляционные ссылки с объектно-ориентированными ссылками.

Созданы два класса (User и Car), каждый из которых не содержит зависимостей от других классов, и его поля могут принимать только следующие типы значений: String, Integer, Double, Boolean, Long.

Указан набор аннотаций для класса и его членов, например, класс User:

```java
@OrmEntity(table = “simple_user”)
public class User {
  @OrmColumnId
  private Long id;
  @OrmColumn(name = “first_name”, length = 10)
  private String firstName;
  @OrmColumn(name = “last_name”, length = 10)
  private String lastName;
  @OrmColumn(name “age”)
  private Integer age;
  ...
}
```

Создан класс OrmManager, который генерирует и выполняет соответствующий SQL-код во время инициализации всех классов, отмеченных аннотацией @OrmEntity. Этот код содержит команду CREATE TABLE для создания таблицы с именем, указанным в аннотации.

Каждое поле класса, отмеченное аннотацией @OrmColumn, становится столбцом в этой таблице. Поле, отмеченное аннотацией @OrmColumnId, указывает на то, что необходимо создать идентификатор автоматического увеличения.

OrmManager поддерживает следующий набор операций (для каждой из них генерируется соответствующий SQL-код в Runtime):

```java
public void save(Object entity)

public void update(Object entity)

public <T> T findById(Long id, Class<T> aClass)
```

- OrmManager обеспечивает вывод сгенерированного SQL на консоль во время выполнения.
- При инициализации OrmManager удаляет созданные таблицы.
- Метод обновления заменяет значения в столбцах, указанных в сущности, даже если значение поля объекта равно нулю.
