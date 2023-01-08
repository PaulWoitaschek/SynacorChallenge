package de.woitaschek.synacorchallenge

import java.util.Stack

class VirtualMachine(memory: List<Int>) {

  private val memory = memory.toMutableList()

  private val registers = IntArray(8) { 0 }
  private val stack = Stack<Int>()

  constructor(byteArray: ByteArray) : this(
    byteArray.toList().chunked(2)
      .map { (byte1, byte2) ->
        val lowByte = byte1.toInt() and 0xff
        val highByte = byte2.toInt() and 0xff
        (highByte shl 8) or lowByte
      },
  )

  operator fun get(pointer: Int): Int = when (val value = memory[pointer]) {
    in 0..32767 -> value
    in 32768..32775 -> registers[registryIndex(value)]
    else -> error("Invalid value=$pointer")
  }

  private fun registryIndex(pointer: Int): Int = (pointer - 32768)

  fun execute(pointer: Int): Int? {
    fun a() = this[pointer + 1]
    fun b() = this[pointer + 2]
    fun c() = this[pointer + 3]

    fun writeToRegisterA(value: Int) {
      registers[registryIndex(memory[pointer + 1])] = value
    }
    return when (OpCode.values()[this[pointer]]) {
      OpCode.Jmp -> a()
      OpCode.Out -> {
        print(a().toChar())
        pointer + 2
      }
      OpCode.NoOp -> pointer + 1
      OpCode.Halt -> return null
      OpCode.Set -> {
        writeToRegisterA(b())
        pointer + 3
      }
      OpCode.Jt -> if (a() != 0) b() else pointer + 3
      OpCode.Jf -> if (a() == 0) b() else pointer + 3
      OpCode.Push -> {
        stack.push(a())
        pointer + 2
      }
      OpCode.Pop -> {
        writeToRegisterA(stack.pop()!!)
        pointer + 2
      }
      OpCode.Eq -> {
        writeToRegisterA(if (b() == c()) 1 else 0)
        pointer + 4
      }
      OpCode.Gt -> {
        writeToRegisterA(if (b() > c()) 1 else 0)
        pointer + 4
      }
      OpCode.Mult -> {
        writeToRegisterA((b() * c()).mod(32768))
        pointer + 4
      }
      OpCode.Mod -> {
        writeToRegisterA(b() % c())
        pointer + 4
      }
      OpCode.And -> {
        writeToRegisterA((b() and c()).mod(32768))
        pointer + 4
      }
      OpCode.Or -> {
        writeToRegisterA((b() or c()).mod(32768))
        pointer + 4
      }
      OpCode.Not -> {
        writeToRegisterA(b().inv() and 0x7FFF)
        pointer + 3
      }
      OpCode.Rmem -> {
        writeToRegisterA(memory[b()])
        pointer + 3
      }
      OpCode.Wmem -> {
        memory[a()] = b()
        pointer + 3
      }
      OpCode.Call -> {
        stack.add(pointer + 2)
        a()
      }
      OpCode.Ret -> stack.pop()
      OpCode.In -> {
        val answer = answers.first()
        val respond = if (answer.isEmpty()) {
          answers.removeAt(0)
          '\n'
        } else {
          answers[0] = answer.drop(1)
          answer.first()
        }
        writeToRegisterA(respond.code)
        pointer + 2
      }
      OpCode.Add -> {
        writeToRegisterA((b() + c()).mod(32768))
        pointer + 4
      }
    }
  }

  private val answers = mutableListOf(
    "doorway",
    "north",
    "north",
    "bridge",
    "continue",
    "down",
    "west",
    "west",
    "east",
    "east",
    "take empty lantern",
  )
}

fun main() {
  val vm = ClassLoader
    .getSystemResourceAsStream("challenge.bin")!!
    .use {
      val bytes = it.readAllBytes()
      VirtualMachine(bytes)
    }
  var pointer: Int? = 0
  while (pointer != null) {
    pointer = vm.execute(pointer)
  }
}

enum class OpCode {
  Halt, Set, Push, Pop, Eq, Gt, Jmp, Jt, Jf, Add, Mult, Mod, And, Or, Not, Rmem, Wmem, Call, Ret, Out, In, NoOp
}
