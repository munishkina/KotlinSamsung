// ==========================================
// 1. БАЗОВЫЙ КЛАСС СОБЫТИЯ
// ==========================================

abstract class Event(val name: String) {
    abstract fun apply(game: Game): String
}

// ==========================================
// 2. ОБЫЧНОЕ СОБЫТИЕ
// ==========================================

class FixedEvent(
    name: String,
    private val budgetChange: Int = 0,
    private val energyChange: Int = 0,
    private val readinessChange: Int = 0,
    private val alarmChange: Int = 0
) : Event(name) {

    override fun apply(game: Game): String {

        game.changeBudget(budgetChange)
        game.changeEnergy(energyChange)
        game.changeReadiness(readinessChange)
        game.changeAlarm(alarmChange)

        return "Изменения: бюджет $budgetChange, " +
                "энергия $energyChange, " +
                "готовность $readinessChange, " +
                "тревога $alarmChange"
    }
}

// ==========================================
// 3. УСЛОВНОЕ СОБЫТИЕ
// ==========================================

class ConditionalEvent(
    name: String,
    private val effect: (Game) -> String
) : Event(name) {

    override fun apply(game: Game): String {
        return effect(game)
    }
}

// ==========================================
// 4. ИСТОРИЯ ПРОХОЖДЕНИЯ
// ==========================================

data class HistoryEntry(
    val day: Int,
    val action: String,
    val event: String,
    val readinessChange: Int,
    val comment: String? = null
)

// ==========================================
// 5. ОСНОВНОЙ КЛАСС ИГРЫ
// ==========================================

class Game {

    var budget = 100
        private set

    var energy = 10
        private set

    var readiness = 0
        private set

    var alarm = 0
        private set

    var budgetSpent = 0
        private set

    var energySpent = 0
        private set

    val history = mutableListOf<HistoryEntry>()

    // Изменение бюджета
    fun changeBudget(value: Int) {

        if (value < 0) {
            budgetSpent += -value
        }

        budget += value
    }

    // Изменение энергии
    fun changeEnergy(value: Int) {

        if (value < 0) {
            energySpent += -value
        }

        energy = (energy + value).coerceAtMost(10)
    }

    // Изменение готовности
    fun changeReadiness(value: Int) {
        readiness = (readiness + value).coerceIn(0, 100)
    }

    // Изменение тревоги
    fun changeAlarm(value: Int) {
        alarm = (alarm + value).coerceIn(0, 100)
    }

    // Проверка доступности действий
    fun canWork(): Boolean {
        return energy >= 4
    }

    fun canInvest(): Boolean {
        return budget > 30
    }

    fun canRest(): Boolean {
        return energy < 10
    }

    // Проверка поражения
    fun isLost(): Boolean {
        return budget <= 0 ||
                energy <= 0 ||
                alarm >= 100
    }

    // ======================================
    // ВЫПОЛНЕНИЕ ДЕЙСТВИЙ
    // ======================================

    fun performAction(choice: Int): String? {

        when (choice) {

            1 -> {

                if (!canWork()) return null

                changeEnergy(-3)
                changeReadiness(25)

                return "Восстановление инфраструктуры"
            }

            2 -> {

                if (!canInvest()) return null

                changeBudget(-30)
                changeReadiness(25)

                return "Поиск необходимых ресурсов"
            }

            3 -> {

                if (!canRest()) return null

                changeEnergy(5)
                changeAlarm(-10)

                return "Отдых в диспетчерской"
            }
        }

        return null
    }

    // ======================================
    // ОСОБОЕ ПРАВИЛО 03:17
    // ======================================

    fun applyNightRule(day: Int, rested: Boolean): String {

        if (rested) {
            return "Вы переждали ночь. Тревога не увеличилась."
        }

        val increase = if (day >= 5) 15 else 8

        changeAlarm(increase)

        return "03:17. Наступила ночь. Тревога +$increase"
    }
}

// ==========================================
// 6. СОЗДАНИЕ СОБЫТИЙ
// ==========================================

fun createEvents(): List<Event> {

    return listOf(

        // ОБЫЧНЫЕ СОБЫТИЯ

        FixedEvent(
            name = "Авария электросети",
            budgetChange = -10,
            alarmChange = 10
        ),

        FixedEvent(
            name = "Неожиданная поставка",
            budgetChange = 15
        ),

        FixedEvent(
            name = "Сигнал из тоннеля",
            readinessChange = 10,
            alarmChange = 10
        ),

        FixedEvent(
            name = "Отключение света",
            energyChange = -2,
            alarmChange = 5
        ),

        // УСЛОВНЫЕ СОБЫТИЯ

        ConditionalEvent("Кто-то в тоннеле") { game ->

            if (game.energy <= 4) {

                game.changeReadiness(-10)
                game.changeAlarm(15)

                "Вы слишком устали, чтобы реагировать. " +
                        "Готовность -10, тревога +15"

            } else {

                game.changeEnergy(-1)
                game.changeAlarm(5)

                "Вы заметили силуэт и успели скрыться. " +
                        "Энергия -1, тревога +5"
            }
        },

        ConditionalEvent("Тайник механика") { game ->

            if (game.budget >= 40) {

                game.changeBudget(-15)
                game.changeReadiness(15)

                "В тайнике нашлись детали! " +
                        "Бюджет -15, готовность +15"

            } else {

                game.changeAlarm(5)

                "Для получения деталей не хватило денег. " +
                        "Тревога +5"
            }
        },

        ConditionalEvent("Незнакомый пассажир") { game ->

            if (game.readiness >= 50) {

                game.changeReadiness(15)

                "Незнакомец помог восстановить поезд! " +
                        "Готовность +15"

            } else {

                game.changeReadiness(-5)
                game.changeAlarm(10)

                "Незнакомец оказался не тем, за кого себя выдавал. " +
                        "Готовность -5, тревога +10"
            }
        },

        ConditionalEvent("Последнее предупреждение") { game ->

            if (game.alarm >= 60) {

                game.changeAlarm(20)
                game.changeReadiness(-10)

                "Громкоговоритель передаёт странные сигналы. " +
                        "Тревога +20, готовность -10"

            } else {

                game.changeAlarm(10)

                "По громкоговорителю прозвучало предупреждение. " +
                        "Тревога +10"
            }
        }
    )
}

// ==========================================
// 7. СООБЩЕНИЯ КАЖДОГО ДНЯ
// ==========================================

fun getDayMessage(day: Int): String {

    val messages = listOf(
        "На станции необычно тихо.",
        "Вы замечаете, что часы идут назад.",
        "В тоннеле кто-то произносит ваше имя.",
        "На табло появляется ваше место назначения.",
        "Диспетчерская отвечает голосом пропавшего машиниста.",
        "Поезд уже виден из тоннеля.",
        "Двери открываются. У вас последний шанс."
    )

    return messages[day - 1]
}

// ==========================================
// 8. ОТОБРАЖЕНИЕ РЕСУРСОВ
// ==========================================

fun showStatus(game: Game, day: Int) {

    println("\n================================")
    println("             ДЕНЬ $day / 7")
    println("================================")

    println("Бюджет:       ${game.budget}")
    println("Энергия:      ${game.energy}")
    println("Готовность:   ${game.readiness}/100")
    println("Тревога:      ${game.alarm}/100")

    println("--------------------------------")
}

// ==========================================
// 9. МЕНЮ ДЕЙСТВИЙ
// ==========================================

fun showMenu(game: Game) {

    println("\nВыберите действие:")

    println("1. Восстановить инфраструктуру")
    println("   +25 готовности, -3 энергии")

    if (!game.canWork()) {
        println("   Недостаточно энергии!")
    }

    println("\n2. Найти необходимые ресурсы")
    println("   +25 готовности, -30 бюджета")

    if (!game.canInvest()) {
        println("   Недостаточно бюджета!")
    }

    println("\n3. Переждать ночь")
    println("   +5 энергии, -10 тревоги")

    if (!game.canRest()) {
        println("   Энергия уже максимальна!")
    }
}

// ==========================================
// 10. ИСТОРИЯ ПРОХОЖДЕНИЯ
// ==========================================

fun showHistory(game: Game) {

    println("\n================================")
    println("       ИСТОРИЯ ПРОХОЖДЕНИЯ")
    println("================================")

    game.history.forEach { entry ->

        println("\nДень ${entry.day}")
        println("Действие: ${entry.action}")
        println("Событие: ${entry.event}")
        println("Изменение готовности: ${entry.readinessChange}")
    }
}

// ==========================================
// 11. СТАТИСТИКА
// ==========================================

fun showStatistics(game: Game) {

    if (game.history.isEmpty()) return

    println("\n================================")
    println("             СТАТИСТИКА")
    println("================================")

    val sortedHistory = game.history.sortedBy {
        it.readinessChange
    }

    val worstDay = sortedHistory.first()
    val bestDay = sortedHistory.last()

    println(
        "Лучший день: ${bestDay.day}, " +
                "изменение: ${bestDay.readinessChange}"
    )

    println(
        "Худший день: ${worstDay.day}, " +
                "изменение: ${worstDay.readinessChange}"
    )

    val positiveDays = game.history.filter {
        it.readinessChange > 0
    }

    val negativeDays = game.history.filter {
        it.readinessChange < 0
    }

    println("\nУспешных дней: ${positiveDays.count()}")
    println("Неудачных дней: ${negativeDays.count()}")

    val totalChange = game.history.sumOf {
        it.readinessChange
    }

    println("Общее изменение готовности: $totalChange")

    println("\n========== ЗАТРАТЫ ==========")

    println("Потрачено бюджета: ${game.budgetSpent}")
    println("Потрачено энергии: ${game.energySpent}")

    println("Остаток бюджета: ${game.budget}")
    println("Остаток энергии: ${game.energy}")

    println("Итоговая тревога: ${game.alarm}")
    println("Итоговая готовность: ${game.readiness}")
}

// ==========================================
// 12. ГЛАВНАЯ ФУНКЦИЯ
// ==========================================

fun main() {

    val game = Game()
    val events = createEvents()

    println("""
        
        ========================================
             7 ДНЕЙ ДО ПОСЛЕДНЕГО ПОЕЗДА
        ========================================
        
        Ты оказался на заброшенной станции.
        Через 7 дней прибудет последний поезд.
        
        Твоя задача — подготовить его к отправлению.
        Для победы необходимо набрать 100 готовности.
        
        Следи за бюджетом, энергией и тревогой.
        
        Особое правило: 03:17.
        Каждую ночь город становится опаснее.
        
        Удачи. Она тебе понадобится...
        
    """.trimIndent())

    // Игровой цикл на 7 дней

    for (day in 1..7) {

        if (game.isLost()) break

        showStatus(game, day)

        println("\n${getDayMessage(day)}")

        val previousReadiness = game.readiness

        var actionName = ""
        var selectedChoice = 0

        // Выбор действия

        while (actionName.isEmpty()) {

            showMenu(game)

            print("\nВведите номер действия (1, 2 или 3): ")

            val choice = readlnOrNull()?.toIntOrNull()

            if (choice == null || choice !in 1..3) {

                println("Ошибка! Нужно ввести 1, 2 или 3.")
                continue
            }

            val result = game.performAction(choice)

            if (result == null) {

                println("Это действие сейчас недоступно!")

            } else {

                actionName = result
                selectedChoice = choice
            }
        }

        println("\nВы выбрали: $actionName")

        // Случайное событие

        val event = events.random()

        println("\nСОБЫТИЕ ДНЯ: ${event.name}")

        val eventResult = event.apply(game)

        println(eventResult)

        // Особое правило 03:17

        println("\nНаступает ночь...")

        val rested = selectedChoice == 3

        val nightResult = game.applyNightRule(day, rested)

        println(nightResult)

        // Изменение готовности за день

        val totalChange = game.readiness - previousReadiness

        // Сохранение истории

        game.history.add(
            HistoryEntry(
                day = day,
                action = actionName,
                event = event.name,
                readinessChange = totalChange
            )
        )

        println("\nИтог дня: $totalChange готовности")

        if (game.isLost()) {
            break
        }
    }

    // ======================================
    // 13. ЗАВЕРШЕНИЕ ИГРЫ
    // ======================================

    println("\n========================================")

    if (game.isLost()) {

        println("              ПОРАЖЕНИЕ")
        println("\nПоследний поезд ушёл без тебя.")
        println("Станция окончательно погрузилась во тьму.")

        if (game.budget <= 0) {
            println("Причина: закончился бюджет.")
        }

        if (game.energy <= 0) {
            println("Причина: закончилась энергия.")
        }

        if (game.alarm >= 100) {
            println("Причина: уровень тревоги достиг максимума.")
        }

    } else if (game.readiness >= 100) {

        println("                ПОБЕДА")
        println("\nТы успел подготовить поезд!")
        println("Двери открываются.")
        println("Ты заходишь внутрь.")
        println("\nНа табло появляется надпись:")
        println("\"Следующая станция: неизвестно\"")

    } else {

        println("              ПОРАЖЕНИЕ")
        println("\nСемь дней закончились.")
        println("Поезд не был готов к отправлению.")
        println("Ты остался на станции...")
    }

    // ======================================
    // 14. ИСТОРИЯ И СТАТИСТИКА
    // ======================================

    showHistory(game)

    showStatistics(game)

    println("\n========================================")
    println("              КОНЕЦ ИГРЫ")
    println("========================================")
}
