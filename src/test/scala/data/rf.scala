package data

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class RFSpec extends AnyFreeSpec with Matchers {
  "RF test" in {
    simulate(new RF) { dut => 
        dut.in.we.poke(true.B)
        dut.in.wR.poke(1.U)
        dut.in.wsel.poke(RF.SEL.IMM)
        dut.in.from_imm.poke(42.U)
        dut.clock.step()
        
        dut.in.rR1.poke(1.U)
        dut.in.rR2.poke(0.U)
        dut.clock.step()
        dut.out.rD1.expect(42.U)
        dut.out.rD2.expect(0.U)

        dut.in.we.poke(true.B)
        dut.in.wR.poke(2.U)
        dut.in.wsel.poke(RF.SEL.ALU)
        dut.in.from_alu.poke(43.U)
        dut.clock.step()

        dut.in.rR1.poke(1.U)
        dut.in.rR2.poke(2.U)
        dut.clock.step()
        dut.out.rD1.expect(42.U)
        dut.out.rD2.expect(43.U)
        
        dut.in.we.poke(false.B)
        dut.in.from_alu.poke(44.U)
        dut.clock.step()
        dut.out.rD2.expect(43.U)
        
    }
  }
  
  "Disallow write zero" in {
    simulate(new RF) { dut => 
        dut.in.we.poke(true.B)
        dut.in.wR.poke(0.U)
        dut.in.wsel.poke(RF.SEL.IMM)
        dut.in.from_imm.poke(42.U)
        dut.clock.step()

        dut.in.rR1.poke(0.U)
        dut.in.rR2.poke(0.U)
        dut.clock.step()
        dut.out.rD1.expect(0.U)
        dut.out.rD2.expect(0.U)
    }
  }
}