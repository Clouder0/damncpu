// Generated by CIRCT firtool-1.62.0
module Bridge(
  input         clock,
                reset,
                cpu_we,
  input  [31:0] cpu_addr,
                cpu_data_from_cpu,
  output [31:0] cpu_data_to_cpu,
  output        dram_we,
  output [31:0] dram_addr,
  input  [31:0] dram_data_from_dram,
  output [31:0] dram_data_to_dram
);

  wire access_mem = cpu_addr[31:12] != 20'hFFFFF;
  assign cpu_data_to_cpu = access_mem ? dram_data_from_dram : 32'hFFFFFFFF;
  assign dram_we = cpu_we & access_mem;
  assign dram_addr = cpu_addr;
  assign dram_data_to_dram = cpu_data_from_cpu;
endmodule

