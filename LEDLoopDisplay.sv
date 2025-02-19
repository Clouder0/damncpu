// Generated by CIRCT firtool-1.62.0
module Digit(
  input  [3:0] in_digit,
  output [7:0] out_seg
);

  wire [15:0][7:0] _GEN =
    '{8'h71,
      8'h61,
      8'h85,
      8'hE5,
      8'hC1,
      8'h11,
      8'h19,
      8'h1,
      8'h1F,
      8'h41,
      8'h49,
      8'h99,
      8'hD,
      8'h25,
      8'h9F,
      8'h3};
  assign out_seg = _GEN[in_digit];
endmodule

module LEDLoopDisplay(
  input         clock,
                reset,
  input  [11:0] in_addr,
  input         in_we,
  input  [31:0] in_wdata,
  output [7:0]  out_en_sel,
                out_seg
);

  wire [7:0]      _digit_display_7_out_seg;
  wire [7:0]      _digit_display_6_out_seg;
  wire [7:0]      _digit_display_5_out_seg;
  wire [7:0]      _digit_display_4_out_seg;
  wire [7:0]      _digit_display_3_out_seg;
  wire [7:0]      _digit_display_2_out_seg;
  wire [7:0]      _digit_display_1_out_seg;
  wire [7:0]      _digit_display_out_seg;
  reg  [31:0]     display_data;
  reg  [14:0]     refresher_value;
  reg  [2:0]      idx;
  wire [7:0][7:0] _GEN =
    {{_digit_display_7_out_seg},
     {_digit_display_6_out_seg},
     {_digit_display_5_out_seg},
     {_digit_display_4_out_seg},
     {_digit_display_3_out_seg},
     {_digit_display_2_out_seg},
     {_digit_display_1_out_seg},
     {_digit_display_out_seg}};
  always @(posedge clock) begin
    if (reset) begin
      display_data <= 32'h0;
      refresher_value <= 15'h0;
      idx <= 3'h0;
    end
    else begin
      if (in_we)
        display_data <= in_wdata;
      if (refresher_value == 15'h752F) begin
        refresher_value <= 15'h0;
        if (&idx)
          idx <= 3'h0;
        else
          idx <= idx + 3'h1;
      end
      else
        refresher_value <= refresher_value + 15'h1;
    end
  end // always @(posedge)
  Digit digit_display (
    .in_digit (display_data[3:0]),
    .out_seg  (_digit_display_out_seg)
  );
  Digit digit_display_1 (
    .in_digit (display_data[7:4]),
    .out_seg  (_digit_display_1_out_seg)
  );
  Digit digit_display_2 (
    .in_digit (display_data[11:8]),
    .out_seg  (_digit_display_2_out_seg)
  );
  Digit digit_display_3 (
    .in_digit (display_data[15:12]),
    .out_seg  (_digit_display_3_out_seg)
  );
  Digit digit_display_4 (
    .in_digit (display_data[19:16]),
    .out_seg  (_digit_display_4_out_seg)
  );
  Digit digit_display_5 (
    .in_digit (display_data[23:20]),
    .out_seg  (_digit_display_5_out_seg)
  );
  Digit digit_display_6 (
    .in_digit (display_data[27:24]),
    .out_seg  (_digit_display_6_out_seg)
  );
  Digit digit_display_7 (
    .in_digit (display_data[31:28]),
    .out_seg  (_digit_display_7_out_seg)
  );
  assign out_en_sel = ~(8'h1 << idx);
  assign out_seg = _GEN[idx];
endmodule

