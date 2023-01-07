package de.woitaschek.synacorchallenge

fun main() {
  val memory = ClassLoader
    .getSystemResourceAsStream("challenge.bin")!!
    .readAllBytes()
    .toList()
    .chunked(2)
    .map { (byte1, byte2) ->
      val lowByte = byte1.toInt() and 0xff
      val highByte = byte2.toInt() and 0xff
      (highByte shl 8) or lowByte
    }

  val registers = IntArray(8) { 0 }
  var pointer = 0
  while (true) {
    fun read(index: Int): Int = when (val value = memory[index]) {
      in 0..32767 -> {
        value
      }
      in 32768..32775 -> {
        val registerIndex = value - 32768
        registers[registerIndex]
      }
      else -> {
        error("Invalid value=$value")
      }
    }
    check(pointer in 0..32767)

    fun a() = read(pointer + 1)
    fun b() = read(pointer + 2)
    pointer = when (OpCode.values()[memory[pointer]]) {
      OpCode.Jmp -> {
        a()
      }
      OpCode.Out -> {
        print(a().toChar())
        pointer + 2
      }
      OpCode.NoOp -> {
        pointer + 1
      }
      OpCode.Halt -> {
        return
      }
      OpCode.Set -> {
        registers[a()] = b()
        pointer + 3
      }
      OpCode.Jt -> {
        if (a() != 0) b() else pointer + 3
      }
      OpCode.Jf -> {
        if (a() == 0) b() else pointer + 3
      }
      OpCode.Push -> TODO()
      OpCode.Pop -> TODO()
      OpCode.Eq -> TODO()
      OpCode.Gt -> TODO()
      OpCode.Mult -> TODO()
      OpCode.Mod -> TODO()
      OpCode.And -> TODO()
      OpCode.Or -> TODO()
      OpCode.Not -> TODO()
      OpCode.Rmem -> TODO()
      OpCode.Wmem -> TODO()
      OpCode.Call -> TODO()
      OpCode.Ret -> TODO()
      OpCode.In -> TODO()
      OpCode.Add -> TODO()
    }
  }
}

enum class OpCode {
  Halt, Set, Push, Pop, Eq, Gt, Jmp, Jt, Jf, Add, Mult, Mod, And, Or, Not, Rmem, Wmem, Call, Ret, Out, In, NoOp
}
