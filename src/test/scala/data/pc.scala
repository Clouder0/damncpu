package data

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class PCSpec extends AnyFreeSpec with Matchers {
  "PC should output the input value" in {
    simulate(new PC) { dut =>
      dut.io.din.poke(42.U)
      dut.clock.step()
      dut.io.pc.expect(42.U)
      dut.io.din.poke(43.U)
      dut.clock.step()
      dut.io.pc.expect(43.U)
    }
  }
}
