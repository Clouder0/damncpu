import data._

import chisel3._
import chisel3.util.Fill
import chisel3.experimental.hierarchy.instantiable
import chisel3.experimental.hierarchy.public

@instantiable
class Bridge extends Module {
  @public val cpu = IO(new Bundle {
    val we = Input(Bool())
    val addr = Input(UInt(32.W))
    val data_from_cpu = Input(UInt(32.W))
    val data_to_cpu = Output(UInt(32.W))
  })

  @public val dram = IO(new Bundle {
    val we = Output(Bool())
    val addr = Output(UInt(32.W))
    val data_from_dram = Input(UInt(32.W))
    val data_to_dram = Output(UInt(32.W))
  })

  val access_mem = Wire(Bool())
  access_mem := cpu.addr(31, 12) === Fill(20, 1.U)

  dram.addr := cpu.addr
  dram.we := cpu.we && access_mem
  dram.data_to_dram := cpu.data_from_cpu

  when(access_mem) {
    cpu.data_to_cpu := dram.data_from_dram
  }.otherwise {
    cpu.data_to_cpu := Fill(32, 1.U)
  }
}
