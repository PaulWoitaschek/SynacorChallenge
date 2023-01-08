package de.woitaschek.synacorchallenge

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class VirtualMachineTest {

  @Test
  fun test() {
    val vm = VirtualMachine("9,32768,32769,4,19,32768,0".split(",").map { it.toInt() })

    var pointer: Int? = 0
    while (pointer != null) {
      println("pointer=$pointer")
      pointer = vm.execute(pointer)
    }
    assertTrue(vm[32768]==4)
  }
}

