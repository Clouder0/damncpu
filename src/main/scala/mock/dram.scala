package mock

import chisel3._
import chisel3.experimental.hierarchy.instantiable
import chisel3.experimental.hierarchy.public

@instantiable
class DRAM extends Module {
  @public val in = IO(new Bundle {
    val addr = Input(UInt(32.W))
    val din = Input(UInt(32.W))
    val we = Input(Bool())
  })
  @public val out = IO(new Bundle {
    val dout = Output(UInt(32.W))
  })
  val mem = Mem(1024, UInt(32.W))
  when(in.we) {
    mem.write(in.addr, in.din)
  }
  out.dout := mem.read(in.addr)
}
