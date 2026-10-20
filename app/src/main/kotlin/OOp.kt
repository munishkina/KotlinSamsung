//класс как структура определяет то, какими свойствами и методами должны обладать все его потомки или экземпляры
class oldCar {
    //переменные внутри класса - свойства
    var wheel: Int = 4
    var steeringWheel = true
    var brand = "Lada"
}

//пример класса с конструктором
class Car(var brand: String, val wheel: Int=4, val power: Double=1.0) {
     //блок init выполняется один раз при создании экземпляра класса
     init{
         //поле это будет доступно только тут и обратиться к нему из объекта класса невозможно
         val newCar = true
       }
    //метод - функция внутри класса

     fun ride(){
         println("Ride $brand")
     }

    override fun toString(): String {
        return "brand: $brand, wheel: $wheel, power: $power"
    }
}

//класс с демонстарцией принципы инкапсуляции
class Person(public var name: String, private var age: Int) {
      //метод гетер предназначен для получения доступа к значению свойства
    fun getAge(): Int{
        return age
      }
    //сеттер для изменения значения свойства
    fun setAge(age: Int) {
      //this - ключевое слово для явного обращения к свойству класса
        this.age = age
    }

    //более простой способ задания геттера и сеттера
    var lastName: String = "Дерден"
        private set

}


open class animal(val name: String, var age: Int, var type: String) {
    open fun sound(){ }
}

class Dog(name: String, age: Int, type: String) : animal(name,age,type){
    override fun sound(){
        println ("гав")
    }
}

class Rat(name: String, age: Int, type: String) : animal(name,age,type){
    override fun sound(){
        super.sound()
    }
}
fun main() {
   //экземпляр класса - это его реализация в виде переменных
    val car1=oldCar()
    val car2=oldCar()

    car2.brand = "Moskvich"
    car2.wheel = 5

    println("Эта машина бренда ${car1.brand},она имеет ${car1.wheel} колеса")
    println("Эта машина бренда ${car2.brand},она имеет ${car2.wheel} колес")

    println(car1)
    println(car2)

    val newCar1=Car(brand = "Volga", power=4.4)
    val newCar2=Car(brand = "Е-мобиль", power =0.0)
    newCar1.brand = "Marusya"

    println("Эта машина бренда ${newCar1.brand},она имеет ${newCar1.wheel} колеса")
    println("Эта машина бренда ${newCar2.brand},она имеет ${newCar2.wheel} колесa")
    newCar1.ride()

    //работа с инкапсуляцией
    val person = Person(name = "Patrick", age=30)
    person.name = "Тайлер"
    println(person.getAge())
    person.setAge(29)
    println(person.getAge())



    val cat=animal(name = "Гав", age=30, type = "Кошка")
    val dog=animal(name = "Рыжик", age=20, type = "Собака")
    val bober=animal(name = "Нот", age=5, type = "Бобер")
    val rat=Rat(name = "Rat", age=20, type = "Rat")

    cat.sound()
    dog.sound()
    bober.sound()

}