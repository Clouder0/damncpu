package data

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class PCSpec extends AnyFreeSpec with Matchers {
  "PC should output the input value" in {
    simulate(new PC) { dut =>
      dut.in.din.poke(42.U)
      dut.clock.step()
      dut.out.pc.expect(42.U)
      dut.in.din.poke(43.U)
      dut.clock.step()
      dut.out.pc.expect(43.U)
    }
  }

  "NPC test" in {
    simulate(new NPC) { dut =>
      dut.in.pc.poke(4.U)
      dut.in.op.poke(NPC.OP.PC4)
      dut.clock.step()
      dut.out.npc.expect(8.U)

      dut.in.op.poke(NPC.OP.BR)
      dut.in.offset.poke(2.U)
      dut.clock.step()
      dut.out.npc.expect(6.U)

      dut.in.op.poke(NPC.OP.PC4)
      dut.clock.step()
      dut.out.npc.expect(8.U)

      dut.in.op.poke(NPC.OP.J)
      dut.in.offset.poke(4.U)
      dut.clock.step()
      dut.out.npc.expect(8.U)

      dut.in.op.poke(NPC.OP.JR)
      dut.in.alu_res.poke(12.U)
      dut.clock.step()
      dut.out.npc.expect(12.U)
    }
  }
}
