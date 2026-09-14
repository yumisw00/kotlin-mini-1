import kotlin.properties.Delegates
import kotlin.random.Random

// ============================================================
// TYPE ALIAS
// Materi: Type Alias
// ============================================================
typealias Damage = Int
typealias CharacterList = MutableList<Character>
typealias DamageCalculator = (Character, Character) -> Damage

// ============================================================
// ANNOTATION
// Materi: Annotation, @Target, @Retention
// ============================================================
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class UltimateSkill(val manaCost: Int = 25)

// ============================================================
// ENUM CLASS
// Materi: Enum Class, Properties & Function di Enum Class
// ============================================================
enum class Element(val label: String) {
    FIRE("Api"),
    WATER("Air"),
    GRASS("Rumput");

    fun advantageAgainst(target: Element): Double {
        return when {
            this == FIRE && target == GRASS -> 1.3
            this == WATER && target == FIRE -> 1.3
            this == GRASS && target == WATER -> 1.3

            this == FIRE && target == WATER -> 0.8
            this == WATER && target == GRASS -> 0.8
            this == GRASS && target == FIRE -> 0.8

            else -> 1.0
        }
    }
}

// ============================================================
// EXCEPTION
// Materi: Exception, Try Catch, Multiple Catch, Finally
// ============================================================
class NotEnoughManaException(message: String) : Exception(message)

// ============================================================
// INLINE / VALUE CLASS
// Materi: Inline Class
// Catatan: Kotlin JVM modern menggunakan @JvmInline value class
// ============================================================
@JvmInline
value class Gold(val amount: Int) {
    operator fun plus(other: Gold): Gold = Gold(amount + other.amount)
}

// ============================================================
// DATA CLASS
// Materi: Data Class, Copy Data Class, Destructuring Declarations
// ============================================================
data class Skill(
    val name: String,
    val manaCost: Int,
    val multiplier: Double
)

data class Position(
    val x: Int,
    val y: Int
)

// ============================================================
// SEALED CLASS
// Materi: Sealed Class, Sealed Class di When Expression
// ============================================================
sealed class BattleResult {
    data class Hit(val damage: Damage, val critical: Boolean = false) : BattleResult()
    data class Miss(val reason: String) : BattleResult()
    object Victory : BattleResult()
}

// ============================================================
// INTERFACE
// Materi: Interface, Inheritance antar Interface,
// Multiple Inheritance dengan Interface, Konflik di Interface
// ============================================================
interface Named {
    val name: String
    fun displayName(): String = name
}

interface Skillable : Named {
    val skill: Skill
    fun useSkill(target: Character): Damage
}

interface BattleLogger {
    fun log(message: String)
}

interface BattleAction {
    fun perform(): String
}

interface SwordStyle {
    fun style(): String = "Sword"
}

interface ShieldStyle {
    fun style(): String = "Shield"
}

// ============================================================
// DELEGATION
// Materi: Delegation, Override Delegation
// ============================================================
class ConsoleLogger : BattleLogger {
    override fun log(message: String) {
        println("[LOG] $message")
    }
}

class BattleLoggerDecorator(private val logger: BattleLogger) : BattleLogger by logger {
    override fun log(message: String) {
        logger.log("[BATTLE] $message")
    }
}

// ============================================================
// SINGLETON OBJECT
// Materi: Singleton Object
// ============================================================
object BattleConfig {
    const val MAX_ROUNDS = 20
    fun opening(): String = "=== RPG OOP Kotlin Terminal ==="
}

// ============================================================
// OPERATOR OVERLOADING PADA CLASS BIASA
// Materi: Operator Overloading, In Operator, Index Access Operator,
// Assignment Operator
// ============================================================
class Inventory {
    private val items = mutableListOf<String>()

    operator fun plusAssign(item: String) {
        items.add(item)
    }

    operator fun get(index: Int): String = items[index]

    operator fun contains(item: String): Boolean = items.contains(item)

    fun all(): List<String> = items.toList()
}

// ============================================================
// ABSTRACT CLASS
// Materi: Abstract Class, Abstract Properties & Function,
// Inheritance, Function Overriding, Properties Overriding,
// Getter Setter, Visibility Modifiers, This Keyword,
// Function Overloading, toString, Any Class, Polymorphism
// ============================================================
abstract class Character(
    override val name: String,
    val maxHp: Int,
    val attackPower: Int,
    val element: Element
) : Named {

    var hp: Int = maxHp
        protected set

    var position: Position = Position(0, 0)
        internal set

    open val description: String = "Character"

    val isAlive: Boolean
        get() = hp > 0

    init {
        require(maxHp > 0) { "maxHp harus lebih dari 0" }
    }

    // Function overloading
    fun info(): String = info(true)

    fun info(verbose: Boolean): String {
        return if (verbose) {
            "$name | HP: $hp/$maxHp | ATK: $attackPower | Element: ${element.label}"
        } else {
            name
        }
    }

    open fun attack(target: Character): Damage {
        val baseDamage = Random.nextInt(attackPower - 3, attackPower + 4)
            .coerceAtLeast(1)

        val multiplier = element.advantageAgainst(target.element)
        val damage = (baseDamage * multiplier).toInt().coerceAtLeast(1)

        target.receiveDamage(damage)
        return damage
    }

    fun receiveDamage(damage: Damage) {
        hp = (hp - damage).coerceAtLeast(0)
    }

    fun heal(amount: Int) {
        hp = (hp + amount).coerceAtMost(maxHp)
    }

    // Operator overloading: -=
    operator fun minusAssign(damage: Damage) {
        receiveDamage(damage)
    }

    // Operator overloading: +=
    operator fun plusAssign(amount: Int) {
        heal(amount)
    }

    // Operator overloading: invoke()
    operator fun invoke(): String {
        return "$name siap bertarung!"
    }

    // Operator overloading: comparison >
    operator fun compareTo(other: Character): Int {
        return hp.compareTo(other.hp)
    }

    fun hpBar(width: Int = 20): String {
        val filled = ((hp.toFloat() / maxHp) * width).toInt().coerceIn(0, width)
        val empty = width - filled

        return "[" + "█".repeat(filled) + " ".repeat(empty) + "] $hp/$maxHp"
    }

    override fun toString(): String {
        return "$name (${element.label}) HP: $hp/$maxHp"
    }
}

// ============================================================
// EXTENSION FUNCTION & EXTENSION PROPERTIES
// Materi: Extension Function, Extension Properties,
// Nullable Extension Function
// ============================================================
fun Character.isLowHp(): Boolean = hp.toFloat() / maxHp < 0.3f

val Character.hpPercent: Int
    get() = (hp * 100 / maxHp).coerceIn(0, 100)

fun String?.toBattleLog(): String = this ?: "Tidak ada log"

// ============================================================
// KONFLIK DI INTERFACE
// Materi: Memperbaiki Konflik di Interface
// ============================================================
class Knight : SwordStyle, ShieldStyle {
    override fun style(): String {
        return super<SwordStyle>.style() + " + " + super<ShieldStyle>.style()
    }
}

// ============================================================
// HERO CLASS
// Materi: Class, Object, Properties, Constructor,
// Secondary Constructor, Initializer Block, This Keyword,
// Inheritance, Function Overriding, Super Keyword,
// Interface, Visibility Modifiers, Getter Setter,
// Late-Initialized Properties, Companion Object, Inner Object,
// Inner Class, Data Class, Annotation
// ============================================================
@UltimateSkill(manaCost = 25)
class Hero(
    name: String,
    maxHp: Int,
    attackPower: Int,
    element: Element
) : Character(name, maxHp, attackPower, element), Skillable, SwordStyle, ShieldStyle {

    var mp: Int = 100
        private set

    var title: String = "Adventurer"
        get() = field.uppercase()
        set(value) {
            field = value.trim()
        }

    override val description: String = "Hero"

    override val skill: Skill = Skill(
        name = "Meteor Slash",
        manaCost = 25,
        multiplier = 2.1
    )

    private val inventory = Inventory()

    var weapon: String = "Rusty Sword"

    init {
        title = "  adventurer "
        inventory += "Potion"

        println("Hero ${this.name} dibuat. Weapon: $weapon, Title: $title")
    }

    // Secondary constructor
    constructor(name: String, maxHp: Int, attackPower: Int) :
            this(name, maxHp, attackPower, Element.FIRE)

    // Konflik interface SwordStyle dan ShieldStyle
    override fun style(): String {
        return super<SwordStyle>.style() + " & " + super<ShieldStyle>.style()
    }

    override fun useSkill(target: Character): Damage {
        if (mp < skill.manaCost) {
            throw NotEnoughManaException("Mana $name tidak cukup untuk ${skill.name}")
        }

        mp -= skill.manaCost

        val damage = (attackPower * skill.multiplier).toInt().coerceAtLeast(1)
        target.receiveDamage(damage)

        return damage
    }

    fun rest() {
        mp = (mp + 15).coerceAtMost(100)
    }

    // Function overriding + super
    override fun attack(target: Character): Damage {
        return super.attack(target)
    }

    // toString override + super
    override fun toString(): String {
        return "Hero ${super.toString()} MP: $mp"
    }

    // Companion object
    companion object {
        fun createDefault(): Hero = Hero(
            name = "Arka",
            maxHp = 120,
            attackPower = 16,
            element = Element.FIRE
        )
    }

    // Inner object / nested singleton object
    object SkillCatalog {
        val ultimate = Skill(
            name = "Meteor Slash",
            manaCost = 25,
            multiplier = 2.1
        )
    }

    // Inner class
    inner class HeroDetail {
        fun full(): String {
            return "$name | $title | HP: ${hpBar()} | MP: $mp | Weapon: $weapon"
        }
    }
}

// ============================================================
// MONSTER CLASS
// Materi: Inheritance, Function Overriding, Final Override Function
// ============================================================
class Monster(
    name: String,
    maxHp: Int,
    attackPower: Int,
    element: Element
) : Character(name, maxHp, attackPower, element) {

    override val description: String = "Monster"

    final override fun attack(target: Character): Damage {
        val damage = super.attack(target)

        return if (Random.nextInt(100) < 20) {
            target.receiveDamage(3)
            damage + 3
        } else {
            damage
        }
    }

    fun taunt(): String = "$name mengancam!"
}

// ============================================================
// GAME ENGINE
// Materi: Delegation, Lazy Properties, Observable Properties,
// Late-Initialized Properties, Type Alias, Scope Functions,
// Exception, Try Catch, Multiple Catch, Finally
// ============================================================
class GameEngine(private val logger: BattleLogger) : BattleLogger by logger {

    lateinit var battleLog: MutableList<String>

    var round: Int by Delegates.observable(0) { _, oldValue, newValue ->
        println("Observable round: $oldValue -> $newValue")
    }

    val title: String by lazy {
        "RPG OOP Kotlin Terminal - Full Modul"
    }

    private val characters: CharacterList = mutableListOf()

    fun register(character: Character) {
        characters.add(character)
    }

    fun showCharacters() {
        characters.forEach { println(it) }
    }

    fun isBattleLogInitialized(): Boolean {
        return ::battleLog.isInitialized
    }

    fun start(hero: Hero, monster: Monster) {
        if (!::battleLog.isInitialized) {
            battleLog = mutableListOf()
        }

        log(title)
        log(BattleConfig.opening())
        battleLog.add("Battle started")

        round = 1

        val damageCalculator: DamageCalculator = { attacker, target ->
            (attacker.attackPower - target.attackPower / 4 + Random.nextInt(1, 5))
                .coerceAtLeast(1)
        }

        while (round <= BattleConfig.MAX_ROUNDS && hero.isAlive && monster.isAlive) {
            println("\n--- Round $round ---")
            println("Prediksi damage calculator: ${damageCalculator(hero, monster)}")

            val useSkill = hero.mp >= hero.skill.manaCost && Random.nextBoolean()

            try {
                val damage: Damage = if (useSkill) {
                    hero.useSkill(monster)
                } else {
                    hero.attack(monster)
                }

                val actionName = if (useSkill) {
                    "SKILL ${hero.skill.name}"
                } else {
                    "ATTACK"
                }

                println("${hero.name} menggunakan $actionName, damage: $damage")

                val result: BattleResult = if (damage >= 25) {
                    BattleResult.Hit(damage, critical = true)
                } else {
                    BattleResult.Hit(damage, critical = false)
                }

                printResult(result)

            } catch (e: NotEnoughManaException) {
                println("Caught NotEnoughManaException: ${e.message}")
                val damage = hero.attack(monster)
                println("Akhirnya ${hero.name} menyerang biasa, damage: $damage")

            } catch (e: IllegalStateException) {
                println("Caught IllegalStateException: ${e.message}")

            } finally {
                battleLog.add("Round $round completed")
            }

            if (!monster.isAlive) break

            val monsterDamage = monster.attack(hero)
            println(monster.taunt())
            println("${monster.name} menyerang ${hero.name}, damage: $monsterDamage")

            if (hero.isAlive && hero.isLowHp()) {
                hero.rest()
                println("${hero.name} beristirahat. MP menjadi ${hero.mp}")
            }

            println("${hero.name}    ${hero.hpBar()} MP:${hero.mp}")
            println("${monster.name} ${monster.hpBar()}")

            round++
            Thread.sleep(350)
        }

        val winner = if (hero.isAlive) hero else monster
        log("${winner.name} memenangkan pertempuran!")
        battleLog.add("Battle ended")

        println("\n--- Battle Log ---")
        battleLog.forEachIndexed { index, log ->
            println("${index + 1}. $log")
        }
    }
}

// ============================================================
// TYPE CHECK & CASTS
// Materi: Type Check & Casts, is, !is, Smart Casts,
// Casts di When Expression, Unsafe Casts, Safe Nullable Casts
// ============================================================
fun describeCharacter(character: Character): String {
    return when (character) {
        is Hero -> "Hero ${character.name}, MP ${character.mp}, style: ${character.style()}"
        is Monster -> "Monster ${character.name}, taunt: ${character.taunt()}"
        else -> "Character tidak dikenal"
    }
}

// ============================================================
// SEALED CLASS DI WHEN EXPRESSION
// Materi: Sealed Class di When Expression
// ============================================================
fun printResult(result: BattleResult) {
    when (result) {
        is BattleResult.Hit -> {
            val criticalInfo = if (result.critical) "CRITICAL" else "NORMAL"
            println("Result: HIT $criticalInfo damage=${result.damage}")
        }

        is BattleResult.Miss -> {
            println("Result: MISS reason=${result.reason}")
        }

        BattleResult.Victory -> {
            println("Result: VICTORY")
        }
    }
}

// ============================================================
// DESTRUCTURING DI FUNCTION
// Materi: Destructuring di Function
// ============================================================
fun battleStatus(hero: Hero, monster: Monster): Pair<Int, Int> {
    return hero.hp to monster.hp
}

// ============================================================
// DEMO FITUR OOP
// Materi: Scope Functions, Null Safety, Extension Function,
// Data Class, Destructuring, Reflection, Polymorphism,
// Anonymous Class, Singleton Object, Companion Object,
// Inner Object, Inner Class, Type Alias, dan lain-lain
// ============================================================
fun demoFiturOop(hero: Hero, monster: Monster, logger: BattleLogger) {
    logger.log("Mulai demo fitur OOP sesuai modul")

    // Scope functions: apply, also, let, run, with
    hero.apply {
        title = "Pahlawan Terminal"
    }.also {
        println("Apply + Also -> title: ${it.title}")
    }

    hero.let {
        println("Let -> HP: ${it.hp}")
    }

    hero.run {
        println("Run -> MP: $mp")
    }

    with(hero) {
        println("With -> HP: $hp, MP: $mp")
    }

    // Extension function & extension properties
    println("Extension property hpPercent: ${hero.hpPercent}%")
    println("Extension function isLowHp: ${hero.isLowHp()}")

    val nullMessage: String? = null
    println("Nullable extension: ${nullMessage.toBattleLog()}")

    // Null safety
    val nickname: String? = null
    println("Safe call + Elvis: ${nickname?.uppercase() ?: "Tanpa nickname"}")

    val sureName: String? = hero.name
    println("Double bang (!!): ${sureName!!.uppercase()}")

    // Destructuring data class
    val (skillName, manaCost, multiplier) = hero.skill
    println("Destructuring skill: $skillName, cost=$manaCost, multiplier=$multiplier")

    // Underscore untuk variable tidak digunakan
    val (_, onlyCost, _) = hero.skill
    println("Destructuring dengan underscore: cost saja=$onlyCost")

    // Data class copy, equals, hashCode, toString
    val upgradedSkill = hero.skill.copy(
        name = "Meteor Slash EX",
        multiplier = 2.5
    )
    println("Copy data class: $upgradedSkill")

    val sameSkill = Skill("Meteor Slash", 25, 2.1)
    println("Equals data class: ${hero.skill == sameSkill}")
    println("HashCode data class: ${hero.skill.hashCode()}")

    // Position & internal setter
    hero.position = Position(5, 7)
    val (x, y) = hero.position
    println("Destructuring position: x=$x, y=$y")

    // Inline/value class & operator overloading
    var gold = Gold(100)
    gold += Gold(50)
    println("Value class Gold setelah +=: ${gold.amount}")

    // Operator overloading di Character
    monster -= 5
    println("Monster setelah operator -=: ${monster.hp}")

    monster += 5
    hero += 5
    println("Setelah operator +=: Hero=${hero.hp}, Monster=${monster.hp}")

    println("Invoke operator: ${hero()}")
    println("Comparison operator hero > monster: ${hero > monster}")

    // Inventory: plusAssign, get, contains/in
    val inventory = Inventory()
    inventory += "Potion"
    inventory += "Ether"

    println("Inventory item pertama: ${inventory[0]}")
    println("Contains Potion? ${"Potion" in inventory}")
    println("All items: ${inventory.all()}")

    // Polymorphism
    val characters: CharacterList = mutableListOf(hero, monster)
    characters.forEach { character ->
        println("Polymorphism -> ${character.displayName()} : $character")
    }

    // Type check & smart casts
    println(describeCharacter(hero))
    println(describeCharacter(monster))

    // Safe cast, unsafe cast
    val asCharacter: Character = hero
    val safeHero = asCharacter as? Hero
    val safeMonster = asCharacter as? Monster

    println("Safe cast hero: ${safeHero?.name}")
    println("Safe cast monster: ${safeMonster?.name ?: "bukan monster"}")

    val forcedHero = asCharacter as Hero
    println("Unsafe cast hero: ${forcedHero.name}")

    safeHero?.let {
        println("Safe hero let -> MP: ${it.mp}")
    }

    // Sealed class + when
    printResult(BattleResult.Hit(30, true))
    printResult(BattleResult.Miss("Musuh menghindar"))
    printResult(BattleResult.Victory)

    // Anonymous class
    val action = object : BattleAction {
        override fun perform(): String = "Hero mempersiapkan diri"
    }
    println("Anonymous class: ${action.perform()}")

    // Interface conflict
    val knight = Knight()
    println("Interface conflict resolved: ${knight.style()}")

    // Singleton object, inner object, companion object
    println("Singleton BattleConfig: ${BattleConfig.opening()}")
    println("Inner object Hero.SkillCatalog: ${Hero.SkillCatalog.ultimate}")

    val defaultHero = Hero.createDefault()
    println("Companion object hero: ${defaultHero.name}")

    // Secondary constructor
    val secondaryHero = Hero("Bima", 100, 14)
    println("Secondary constructor hero: ${secondaryHero.info()}")

    // Function overloading
    println("Overloading info(): ${hero.info()}")
    println("Overloading info(false): ${hero.info(false)}")

    // Inner class
    println("Inner class HeroDetail: ${hero.HeroDetail().full()}")

    // Destructuring return function
    val (heroHp, monsterHp) = battleStatus(hero, monster)
    println("Destructuring return function: heroHp=$heroHp, monsterHp=$monsterHp")

    // Destructuring lambda parameter
    val hpMap = mapOf(
        "Hero" to hero.hp,
        "Monster" to monster.hp
    )

    hpMap.forEach { (key, value) ->
        println("Lambda destructuring -> $key HP: $value")
    }

    // Reflection sederhana
    println("Reflection Kotlin: ${hero::class.simpleName}")
    println("Reflection Java: ${hero.javaClass.name}")
    println("Jumlah constructor Java: ${hero.javaClass.constructors.size}")

    val annotation = hero.javaClass.getAnnotation(UltimateSkill::class.java)
    println("Annotation UltimateSkill manaCost: ${annotation?.manaCost}")

    logger.log("Selesai demo fitur OOP")
}

// ============================================================
// MAIN
// ============================================================
fun main() {
    val logger = BattleLoggerDecorator(ConsoleLogger())

    val hero = Hero.createDefault()
    val monster = Monster(
        name = "Goblin",
        maxHp = 95,
        attackPower = 12,
        element = Element.GRASS
    )

    val engine = GameEngine(logger)
    engine.register(hero)
    engine.register(monster)

    println(BattleConfig.opening())
    println(engine.title)

    demoFiturOop(hero, monster, logger)

    println("\nStatus sebelum battle:")
    engine.showCharacters()
    println("lateinit battleLog initialized? ${engine.isBattleLogInitialized()}")

    engine.start(hero, monster)

    println("\nStatus setelah battle:")
    engine.showCharacters()
    println("lateinit battleLog initialized? ${engine.isBattleLogInitialized()}")
}