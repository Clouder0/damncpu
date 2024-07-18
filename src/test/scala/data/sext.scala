package data

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class SEXTSpec extends AnyFreeSpec with Matchers {
  "SEXT test" in {
    simulate(new SEXT) { dut => 
        dut.in.din.poke("b0110100000000000000000000".U)
        dut.in.op.poke(SEXT.OP.I)
        dut.clock.step()
        dut.out.dout.expect("b00000000000000000000011010000000".U)
        
        dut.in.din.poke("b1110100000000000000000000".U)
        dut.in.op.poke(SEXT.OP.I)
        dut.clock.step()
        dut.out.dout.expect("b11111111111111111111111010000000".U)
    }
  }
}