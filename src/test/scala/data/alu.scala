package data

import data._
import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import upack.UInt64

class ALUSpec extends AnyFreeSpec with Matchers {
  "ALU should output the correct value" in {
    simulate(new ALU) { dut =>
      dut.in.a.poke(42.U)
      dut.in.b.poke(43.U)
      dut.in.alu_op.poke(ALU.OP.ADD)
      dut.clock.step()
      dut.out.res.expect(85.U)

      dut.in.a.poke(43.U)
      dut.in.b.poke(41.U)
      dut.in.alu_op.poke(ALU.OP.SUB)
      dut.clock.step()
      dut.out.res.expect(2.U)

      dut.in.a.poke(5.U)
      dut.in.b.poke(1.U)
      dut.in.alu_op.poke(ALU.OP.AND)
      dut.clock.step()
      dut.out.res.expect(1.U)

      dut.in.a.poke(5.U)
      dut.in.b.poke(1.U)
      dut.in.alu_op.poke(ALU.OP.OR)
      dut.clock.step()
      dut.out.res.expect(5.U)

      dut.in.a.poke(5.U)
      dut.in.b.poke(1.U)
      dut.in.alu_op.poke(ALU.OP.XOR)
      dut.clock.step()
      dut.out.res.expect(4.U)

      dut.in.a.poke(5.U)
      dut.in.b.poke(2.U)
      dut.in.alu_op.poke(ALU.OP.SLL)
      dut.clock.step()
      dut.out.res.expect(20.U)

      dut.in.a.poke(5.U)
      dut.in.b.poke(2.U)
      dut.in.alu_op.poke(ALU.OP.SRL)
      dut.clock.step()
      dut.out.res.expect(1.U)

      dut.in.a.poke(5.U)
      dut.in.b.poke(2.U)
      dut.in.alu_op.poke(ALU.OP.SRA)
      dut.clock.step()
      dut.out.res.expect(1.U)

      // negative right shift
      // (-2) in uint32 equals to 4294967294
      dut.in.a.poke("b11111111111111111111111111111110".U) // -2 as UINT
      dut.in.b.poke(1.U)
      dut.in.alu_op.poke(ALU.OP.SRA)
      dut.clock.step()
      dut.out.res.expect("b11111111111111111111111111111111".U)

      dut.in.a.poke(5.U)
      dut.in.b.poke(5.U)
      dut.in.alu_op.poke(ALU.OP.BEQ)
      dut.clock.step()
      dut.out.br.expect(true.B)

      dut.in.a.poke(5.U)
      dut.in.b.poke(4.U)
      dut.in.alu_op.poke(ALU.OP.BEQ)
      dut.clock.step()
      dut.out.br.expect(false.B)

      dut.in.a.poke(5.U)
      dut.in.b.poke(4.U)
      dut.in.alu_op.poke(ALU.OP.BNE)
      dut.clock.step()
      dut.out.br.expect(true.B)

      dut.in.a.poke(5.U)
      dut.in.b.poke(4.U)
      dut.in.alu_op.poke(ALU.OP.BLT)
      dut.clock.step()
      dut.out.br.expect(false.B)
      dut.in.a.poke(4.U)
      dut.in.b.poke(5.U)
      dut.in.alu_op.poke(ALU.OP.BLT)
      dut.clock.step()
      dut.out.br.expect(true.B)

      dut.in.a.poke(4.U)
      dut.in.b.poke(4.U)
      dut.in.alu_op.poke(ALU.OP.BGE)
      dut.clock.step()
      dut.out.br.expect(true.B)

    }
  }
  
  "ALU imm extension" in {
    simulate(new ALU) { dut =>
      dut.in.a.poke(0.U)
      dut.in.imm.poke(1.U)
      dut.in.b.poke(2.U)
      dut.in.sel.poke(true.B)
      dut.in.alu_op.poke(ALU.OP.ADD)
      
      dut.clock.step()
      
      dut.out.res.expect(1.U)
    }
  }
}
