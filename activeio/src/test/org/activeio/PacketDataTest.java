begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   *   * Copyright 2004 Hiram Chirino  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|activeio
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|packet
operator|.
name|ByteArrayPacket
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
specifier|public
class|class
name|PacketDataTest
extends|extends
name|TestCase
block|{
name|ByteArrayPacket
name|packet
init|=
operator|new
name|ByteArrayPacket
argument_list|(
operator|new
name|byte
index|[
literal|200
index|]
argument_list|)
decl_stmt|;
name|PacketData
name|data
init|=
operator|new
name|PacketData
argument_list|(
name|packet
argument_list|)
decl_stmt|;
specifier|public
name|void
name|testInteger
parameter_list|()
throws|throws
name|IOException
block|{
name|data
operator|.
name|writeInt
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|data
operator|.
name|writeInt
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
name|data
operator|.
name|writeInt
argument_list|(
literal|551
argument_list|)
expr_stmt|;
name|packet
operator|.
name|flip
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|data
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
name|data
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|551
argument_list|,
name|data
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

