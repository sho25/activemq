begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|openwire
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|SessionId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
import|;
end_import

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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**  *  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|NumberRangesWhileMarshallingTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|NumberRangesWhileMarshallingTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|String
name|connectionId
init|=
literal|"Cheese"
decl_stmt|;
specifier|protected
name|ByteArrayOutputStream
name|buffer
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
specifier|protected
name|DataOutputStream
name|ds
init|=
operator|new
name|DataOutputStream
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
specifier|protected
name|OpenWireFormat
name|openWireformat
decl_stmt|;
specifier|protected
name|int
name|endOfStreamMarker
init|=
literal|0x12345678
decl_stmt|;
specifier|public
name|void
name|testLongNumberRanges
parameter_list|()
throws|throws
name|Exception
block|{
name|long
index|[]
name|numberValues
init|=
block|{
comment|// bytes
literal|0
block|,
literal|1
block|,
literal|0x7e
block|,
literal|0x7f
block|,
literal|0x80
block|,
literal|0x81
block|,
literal|0xf0
block|,
literal|0xff
block|,
comment|// shorts
literal|0x7eff
block|,
literal|0x7fffL
block|,
literal|0x8001L
block|,
literal|0x8000L
block|,
literal|0xe000L
block|,
literal|0xe0001L
block|,
literal|0xff00L
block|,
literal|0xffffL
block|,
comment|// ints
literal|0x10000L
block|,
literal|0x700000L
block|,
literal|0x12345678L
block|,
literal|0x72345678L
block|,
literal|0x7fffffffL
block|,
literal|0x80000000L
block|,
literal|0x80000001L
block|,
literal|0xE0000001L
block|,
literal|0xFFFFFFFFL
block|,
comment|// 3 byte longs
literal|0x123456781L
block|,
literal|0x1234567812L
block|,
literal|0x12345678123L
block|,
literal|0x123456781234L
block|,
literal|0x1234567812345L
block|,
literal|0x12345678123456L
block|,
literal|0x7e345678123456L
block|,
literal|0x7fffffffffffffL
block|,
literal|0x80000000000000L
block|,
literal|0x80000000000001L
block|,
literal|0xe0000000000001L
block|,
literal|0xffffffffffffffL
block|,
comment|// 4 byte longs
literal|0x1234567812345678L
block|,
literal|0x7fffffffffffffffL
block|,
literal|0x8000000000000000L
block|,
literal|0x8000000000000001L
block|,
literal|0xe000000000000001L
block|,
literal|0xffffffffffffffffL
block|,
literal|1
block|}
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
name|numberValues
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|long
name|value
init|=
name|numberValues
index|[
name|i
index|]
decl_stmt|;
name|SessionId
name|object
init|=
operator|new
name|SessionId
argument_list|()
decl_stmt|;
name|object
operator|.
name|setConnectionId
argument_list|(
name|connectionId
argument_list|)
expr_stmt|;
name|object
operator|.
name|setValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|writeObject
argument_list|(
name|object
argument_list|)
expr_stmt|;
block|}
name|ds
operator|.
name|writeInt
argument_list|(
name|endOfStreamMarker
argument_list|)
expr_stmt|;
comment|// now lets read from the stream
name|ds
operator|.
name|close
argument_list|()
expr_stmt|;
name|ByteArrayInputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|buffer
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
name|DataInputStream
name|dis
init|=
operator|new
name|DataInputStream
argument_list|(
name|in
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
name|numberValues
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|long
name|value
init|=
name|numberValues
index|[
name|i
index|]
decl_stmt|;
name|String
name|expected
init|=
name|Long
operator|.
name|toHexString
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Unmarshaling value: "
operator|+
name|i
operator|+
literal|" = "
operator|+
name|expected
argument_list|)
expr_stmt|;
name|SessionId
name|command
init|=
operator|(
name|SessionId
operator|)
name|openWireformat
operator|.
name|unmarshal
argument_list|(
name|dis
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"connection ID in object: "
operator|+
name|i
argument_list|,
name|connectionId
argument_list|,
name|command
operator|.
name|getConnectionId
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|actual
init|=
name|Long
operator|.
name|toHexString
argument_list|(
name|command
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"value of object: "
operator|+
name|i
operator|+
literal|" was: "
operator|+
name|actual
argument_list|,
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
name|int
name|marker
init|=
name|dis
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Marker int"
argument_list|,
name|Integer
operator|.
name|toHexString
argument_list|(
name|endOfStreamMarker
argument_list|)
argument_list|,
name|Integer
operator|.
name|toHexString
argument_list|(
name|marker
argument_list|)
argument_list|)
expr_stmt|;
comment|// lets try read and we should get an exception
try|try
block|{
name|byte
name|value
init|=
name|dis
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|fail
argument_list|(
literal|"Should have reached the end of the stream"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// worked!
block|}
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|openWireformat
operator|=
name|createOpenWireFormat
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|OpenWireFormat
name|createOpenWireFormat
parameter_list|()
block|{
name|OpenWireFormat
name|wf
init|=
operator|new
name|OpenWireFormat
argument_list|()
decl_stmt|;
name|wf
operator|.
name|setCacheEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|wf
operator|.
name|setStackTraceEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|wf
operator|.
name|setVersion
argument_list|(
literal|1
argument_list|)
expr_stmt|;
return|return
name|wf
return|;
block|}
specifier|private
name|void
name|writeObject
parameter_list|(
name|Object
name|object
parameter_list|)
throws|throws
name|IOException
block|{
name|openWireformat
operator|.
name|marshal
argument_list|(
name|object
argument_list|,
name|ds
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

