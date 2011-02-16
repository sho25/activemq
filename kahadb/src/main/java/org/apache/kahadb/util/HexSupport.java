begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * Used to convert to hex from byte arrays and back.  *   *   */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|HexSupport
block|{
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|HEX_TABLE
init|=
operator|new
name|String
index|[]
block|{
literal|"00"
block|,
literal|"01"
block|,
literal|"02"
block|,
literal|"03"
block|,
literal|"04"
block|,
literal|"05"
block|,
literal|"06"
block|,
literal|"07"
block|,
literal|"08"
block|,
literal|"09"
block|,
literal|"0a"
block|,
literal|"0b"
block|,
literal|"0c"
block|,
literal|"0d"
block|,
literal|"0e"
block|,
literal|"0f"
block|,
literal|"10"
block|,
literal|"11"
block|,
literal|"12"
block|,
literal|"13"
block|,
literal|"14"
block|,
literal|"15"
block|,
literal|"16"
block|,
literal|"17"
block|,
literal|"18"
block|,
literal|"19"
block|,
literal|"1a"
block|,
literal|"1b"
block|,
literal|"1c"
block|,
literal|"1d"
block|,
literal|"1e"
block|,
literal|"1f"
block|,
literal|"20"
block|,
literal|"21"
block|,
literal|"22"
block|,
literal|"23"
block|,
literal|"24"
block|,
literal|"25"
block|,
literal|"26"
block|,
literal|"27"
block|,
literal|"28"
block|,
literal|"29"
block|,
literal|"2a"
block|,
literal|"2b"
block|,
literal|"2c"
block|,
literal|"2d"
block|,
literal|"2e"
block|,
literal|"2f"
block|,
literal|"30"
block|,
literal|"31"
block|,
literal|"32"
block|,
literal|"33"
block|,
literal|"34"
block|,
literal|"35"
block|,
literal|"36"
block|,
literal|"37"
block|,
literal|"38"
block|,
literal|"39"
block|,
literal|"3a"
block|,
literal|"3b"
block|,
literal|"3c"
block|,
literal|"3d"
block|,
literal|"3e"
block|,
literal|"3f"
block|,
literal|"40"
block|,
literal|"41"
block|,
literal|"42"
block|,
literal|"43"
block|,
literal|"44"
block|,
literal|"45"
block|,
literal|"46"
block|,
literal|"47"
block|,
literal|"48"
block|,
literal|"49"
block|,
literal|"4a"
block|,
literal|"4b"
block|,
literal|"4c"
block|,
literal|"4d"
block|,
literal|"4e"
block|,
literal|"4f"
block|,
literal|"50"
block|,
literal|"51"
block|,
literal|"52"
block|,
literal|"53"
block|,
literal|"54"
block|,
literal|"55"
block|,
literal|"56"
block|,
literal|"57"
block|,
literal|"58"
block|,
literal|"59"
block|,
literal|"5a"
block|,
literal|"5b"
block|,
literal|"5c"
block|,
literal|"5d"
block|,
literal|"5e"
block|,
literal|"5f"
block|,
literal|"60"
block|,
literal|"61"
block|,
literal|"62"
block|,
literal|"63"
block|,
literal|"64"
block|,
literal|"65"
block|,
literal|"66"
block|,
literal|"67"
block|,
literal|"68"
block|,
literal|"69"
block|,
literal|"6a"
block|,
literal|"6b"
block|,
literal|"6c"
block|,
literal|"6d"
block|,
literal|"6e"
block|,
literal|"6f"
block|,
literal|"70"
block|,
literal|"71"
block|,
literal|"72"
block|,
literal|"73"
block|,
literal|"74"
block|,
literal|"75"
block|,
literal|"76"
block|,
literal|"77"
block|,
literal|"78"
block|,
literal|"79"
block|,
literal|"7a"
block|,
literal|"7b"
block|,
literal|"7c"
block|,
literal|"7d"
block|,
literal|"7e"
block|,
literal|"7f"
block|,
literal|"80"
block|,
literal|"81"
block|,
literal|"82"
block|,
literal|"83"
block|,
literal|"84"
block|,
literal|"85"
block|,
literal|"86"
block|,
literal|"87"
block|,
literal|"88"
block|,
literal|"89"
block|,
literal|"8a"
block|,
literal|"8b"
block|,
literal|"8c"
block|,
literal|"8d"
block|,
literal|"8e"
block|,
literal|"8f"
block|,
literal|"90"
block|,
literal|"91"
block|,
literal|"92"
block|,
literal|"93"
block|,
literal|"94"
block|,
literal|"95"
block|,
literal|"96"
block|,
literal|"97"
block|,
literal|"98"
block|,
literal|"99"
block|,
literal|"9a"
block|,
literal|"9b"
block|,
literal|"9c"
block|,
literal|"9d"
block|,
literal|"9e"
block|,
literal|"9f"
block|,
literal|"a0"
block|,
literal|"a1"
block|,
literal|"a2"
block|,
literal|"a3"
block|,
literal|"a4"
block|,
literal|"a5"
block|,
literal|"a6"
block|,
literal|"a7"
block|,
literal|"a8"
block|,
literal|"a9"
block|,
literal|"aa"
block|,
literal|"ab"
block|,
literal|"ac"
block|,
literal|"ad"
block|,
literal|"ae"
block|,
literal|"af"
block|,
literal|"b0"
block|,
literal|"b1"
block|,
literal|"b2"
block|,
literal|"b3"
block|,
literal|"b4"
block|,
literal|"b5"
block|,
literal|"b6"
block|,
literal|"b7"
block|,
literal|"b8"
block|,
literal|"b9"
block|,
literal|"ba"
block|,
literal|"bb"
block|,
literal|"bc"
block|,
literal|"bd"
block|,
literal|"be"
block|,
literal|"bf"
block|,
literal|"c0"
block|,
literal|"c1"
block|,
literal|"c2"
block|,
literal|"c3"
block|,
literal|"c4"
block|,
literal|"c5"
block|,
literal|"c6"
block|,
literal|"c7"
block|,
literal|"c8"
block|,
literal|"c9"
block|,
literal|"ca"
block|,
literal|"cb"
block|,
literal|"cc"
block|,
literal|"cd"
block|,
literal|"ce"
block|,
literal|"cf"
block|,
literal|"d0"
block|,
literal|"d1"
block|,
literal|"d2"
block|,
literal|"d3"
block|,
literal|"d4"
block|,
literal|"d5"
block|,
literal|"d6"
block|,
literal|"d7"
block|,
literal|"d8"
block|,
literal|"d9"
block|,
literal|"da"
block|,
literal|"db"
block|,
literal|"dc"
block|,
literal|"dd"
block|,
literal|"de"
block|,
literal|"df"
block|,
literal|"e0"
block|,
literal|"e1"
block|,
literal|"e2"
block|,
literal|"e3"
block|,
literal|"e4"
block|,
literal|"e5"
block|,
literal|"e6"
block|,
literal|"e7"
block|,
literal|"e8"
block|,
literal|"e9"
block|,
literal|"ea"
block|,
literal|"eb"
block|,
literal|"ec"
block|,
literal|"ed"
block|,
literal|"ee"
block|,
literal|"ef"
block|,
literal|"f0"
block|,
literal|"f1"
block|,
literal|"f2"
block|,
literal|"f3"
block|,
literal|"f4"
block|,
literal|"f5"
block|,
literal|"f6"
block|,
literal|"f7"
block|,
literal|"f8"
block|,
literal|"f9"
block|,
literal|"fa"
block|,
literal|"fb"
block|,
literal|"fc"
block|,
literal|"fd"
block|,
literal|"fe"
block|,
literal|"ff"
block|,     }
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|INT_OFFSETS
init|=
operator|new
name|int
index|[]
block|{
literal|24
block|,
literal|16
block|,
literal|8
block|,
literal|0
block|}
decl_stmt|;
specifier|private
name|HexSupport
parameter_list|()
block|{     }
comment|/**      * @param hex      * @return      */
specifier|public
specifier|static
name|byte
index|[]
name|toBytesFromHex
parameter_list|(
name|String
name|hex
parameter_list|)
block|{
name|byte
name|rc
index|[]
init|=
operator|new
name|byte
index|[
name|hex
operator|.
name|length
argument_list|()
operator|/
literal|2
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|rc
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|h
init|=
name|hex
operator|.
name|substring
argument_list|(
name|i
operator|*
literal|2
argument_list|,
name|i
operator|*
literal|2
operator|+
literal|2
argument_list|)
decl_stmt|;
name|int
name|x
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|h
argument_list|,
literal|16
argument_list|)
decl_stmt|;
name|rc
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|x
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
comment|/**      * @param bytes      * @return      */
specifier|public
specifier|static
name|String
name|toHexFromBytes
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|StringBuffer
name|rc
init|=
operator|new
name|StringBuffer
argument_list|(
name|bytes
operator|.
name|length
operator|*
literal|2
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|bytes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|rc
operator|.
name|append
argument_list|(
name|HEX_TABLE
index|[
literal|0xFF
operator|&
name|bytes
index|[
name|i
index|]
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|rc
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      *       * @param value       * @param trim if the leading 0's should be trimmed off.      * @return      */
specifier|public
specifier|static
name|String
name|toHexFromInt
parameter_list|(
name|int
name|value
parameter_list|,
name|boolean
name|trim
parameter_list|)
block|{
name|StringBuffer
name|rc
init|=
operator|new
name|StringBuffer
argument_list|(
name|INT_OFFSETS
operator|.
name|length
operator|*
literal|2
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|INT_OFFSETS
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|b
init|=
literal|0xFF
operator|&
operator|(
name|value
operator|>>
name|INT_OFFSETS
index|[
name|i
index|]
operator|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|trim
operator|&&
name|b
operator|==
literal|0
operator|)
condition|)
block|{
name|rc
operator|.
name|append
argument_list|(
name|HEX_TABLE
index|[
name|b
index|]
argument_list|)
expr_stmt|;
name|trim
operator|=
literal|false
expr_stmt|;
block|}
block|}
return|return
name|rc
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

