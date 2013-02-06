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
name|activemq
operator|.
name|openwire
package|;
end_package

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
name|AssertionFailedError
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
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|BooleanStreamTest
extends|extends
name|TestCase
block|{
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
name|int
name|numberOfBytes
init|=
literal|8
operator|*
literal|200
decl_stmt|;
interface|interface
name|BooleanValueSet
block|{
name|boolean
name|getBooleanValueFor
parameter_list|(
name|int
name|index
parameter_list|,
name|int
name|count
parameter_list|)
function_decl|;
block|}
specifier|public
name|void
name|testBooleanMarshallingUsingAllTrue
parameter_list|()
throws|throws
name|Exception
block|{
name|testBooleanStream
argument_list|(
name|numberOfBytes
argument_list|,
operator|new
name|BooleanValueSet
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|getBooleanValueFor
parameter_list|(
name|int
name|index
parameter_list|,
name|int
name|count
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testBooleanMarshallingUsingAllFalse
parameter_list|()
throws|throws
name|Exception
block|{
name|testBooleanStream
argument_list|(
name|numberOfBytes
argument_list|,
operator|new
name|BooleanValueSet
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|getBooleanValueFor
parameter_list|(
name|int
name|index
parameter_list|,
name|int
name|count
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testBooleanMarshallingUsingOddAlternateTrueFalse
parameter_list|()
throws|throws
name|Exception
block|{
name|testBooleanStream
argument_list|(
name|numberOfBytes
argument_list|,
operator|new
name|BooleanValueSet
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|getBooleanValueFor
parameter_list|(
name|int
name|index
parameter_list|,
name|int
name|count
parameter_list|)
block|{
return|return
operator|(
name|index
operator|&
literal|1
operator|)
operator|==
literal|0
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testBooleanMarshallingUsingEvenAlternateTrueFalse
parameter_list|()
throws|throws
name|Exception
block|{
name|testBooleanStream
argument_list|(
name|numberOfBytes
argument_list|,
operator|new
name|BooleanValueSet
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|getBooleanValueFor
parameter_list|(
name|int
name|index
parameter_list|,
name|int
name|count
parameter_list|)
block|{
return|return
operator|(
name|index
operator|&
literal|1
operator|)
operator|!=
literal|0
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|testBooleanStream
parameter_list|(
name|int
name|numberOfBytes
parameter_list|,
name|BooleanValueSet
name|valueSet
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numberOfBytes
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|assertMarshalBooleans
argument_list|(
name|i
argument_list|,
name|valueSet
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
operator|(
name|AssertionFailedError
operator|)
operator|new
name|AssertionFailedError
argument_list|(
literal|"Iteration failed at: "
operator|+
name|i
argument_list|)
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
specifier|protected
name|void
name|assertMarshalBooleans
parameter_list|(
name|int
name|count
parameter_list|,
name|BooleanValueSet
name|valueSet
parameter_list|)
throws|throws
name|Exception
block|{
name|BooleanStream
name|bs
init|=
operator|new
name|BooleanStream
argument_list|()
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|bs
operator|.
name|writeBoolean
argument_list|(
name|valueSet
operator|.
name|getBooleanValueFor
argument_list|(
name|i
argument_list|,
name|count
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ByteArrayOutputStream
name|buffer
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|DataOutputStream
name|ds
init|=
operator|new
name|DataOutputStream
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
name|bs
operator|.
name|marshal
argument_list|(
name|ds
argument_list|)
expr_stmt|;
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
name|bs
operator|=
operator|new
name|BooleanStream
argument_list|()
expr_stmt|;
try|try
block|{
name|bs
operator|.
name|unmarshal
argument_list|(
name|dis
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Failed to unmarshal: "
operator|+
name|count
operator|+
literal|" booleans: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|boolean
name|expected
init|=
name|valueSet
operator|.
name|getBooleanValueFor
argument_list|(
name|i
argument_list|,
name|count
argument_list|)
decl_stmt|;
comment|// /System.out.println("Unmarshaling value: " + i + " = " + expected
comment|// + " out of: " + count);
try|try
block|{
name|boolean
name|actual
init|=
name|bs
operator|.
name|readBoolean
argument_list|()
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
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Failed to parse boolean: "
operator|+
name|i
operator|+
literal|" out of: "
operator|+
name|count
operator|+
literal|" due to: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
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
literal|"Marker int when unmarshalling: "
operator|+
name|count
operator|+
literal|" booleans"
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
name|dis
operator|.
name|readByte
argument_list|()
expr_stmt|;
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
annotation|@
name|Override
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
block|}
end_class

end_unit

