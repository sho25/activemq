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
name|transport
operator|.
name|amqp
operator|.
name|protocol
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|amqp
operator|.
name|AmqpFrameParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|amqp
operator|.
name|AmqpHeader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|amqp
operator|.
name|AmqpWireFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|hawtbuf
operator|.
name|Buffer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|hawtbuf
operator|.
name|DataByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
specifier|public
class|class
name|AmqpFrameParserTest
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AmqpFrameParserTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|AmqpWireFormat
name|amqpWireFormat
init|=
operator|new
name|AmqpWireFormat
argument_list|()
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Object
argument_list|>
name|frames
decl_stmt|;
specifier|private
name|AmqpFrameParser
name|codec
decl_stmt|;
specifier|private
specifier|final
name|int
name|MESSAGE_SIZE
init|=
literal|5
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|frames
operator|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|()
expr_stmt|;
name|codec
operator|=
operator|new
name|AmqpFrameParser
argument_list|(
operator|new
name|AmqpFrameParser
operator|.
name|AMQPFrameSink
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onFrame
parameter_list|(
name|Object
name|frame
parameter_list|)
block|{
name|frames
operator|.
name|add
argument_list|(
name|frame
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|codec
operator|.
name|setWireFormat
argument_list|(
name|amqpWireFormat
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAMQPHeaderReadEmptyBuffer
parameter_list|()
throws|throws
name|Exception
block|{
name|codec
operator|.
name|parse
argument_list|(
name|ByteBuffer
operator|.
name|allocate
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAMQPHeaderReadNull
parameter_list|()
throws|throws
name|Exception
block|{
name|codec
operator|.
name|parse
argument_list|(
operator|(
name|ByteBuffer
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAMQPHeaderRead
parameter_list|()
throws|throws
name|Exception
block|{
name|AmqpHeader
name|inputHeader
init|=
operator|new
name|AmqpHeader
argument_list|()
decl_stmt|;
name|codec
operator|.
name|parse
argument_list|(
name|inputHeader
operator|.
name|getBuffer
argument_list|()
operator|.
name|toByteBuffer
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|frames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Object
name|outputFrame
init|=
name|frames
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|outputFrame
operator|instanceof
name|AmqpHeader
argument_list|)
expr_stmt|;
name|AmqpHeader
name|outputHeader
init|=
operator|(
name|AmqpHeader
operator|)
name|outputFrame
decl_stmt|;
name|assertHeadersEqual
argument_list|(
name|inputHeader
argument_list|,
name|outputHeader
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAMQPHeaderReadSingleByteReads
parameter_list|()
throws|throws
name|Exception
block|{
name|AmqpHeader
name|inputHeader
init|=
operator|new
name|AmqpHeader
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
name|inputHeader
operator|.
name|getBuffer
argument_list|()
operator|.
name|length
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|codec
operator|.
name|parse
argument_list|(
name|inputHeader
operator|.
name|getBuffer
argument_list|()
operator|.
name|slice
argument_list|(
name|i
argument_list|,
name|i
operator|+
literal|1
argument_list|)
operator|.
name|toByteBuffer
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|frames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Object
name|outputFrame
init|=
name|frames
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|outputFrame
operator|instanceof
name|AmqpHeader
argument_list|)
expr_stmt|;
name|AmqpHeader
name|outputHeader
init|=
operator|(
name|AmqpHeader
operator|)
name|outputFrame
decl_stmt|;
name|assertHeadersEqual
argument_list|(
name|inputHeader
argument_list|,
name|outputHeader
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testResetReadsNextAMQPHeaderMidParse
parameter_list|()
throws|throws
name|Exception
block|{
name|AmqpHeader
name|inputHeader
init|=
operator|new
name|AmqpHeader
argument_list|()
decl_stmt|;
name|DataByteArrayOutputStream
name|headers
init|=
operator|new
name|DataByteArrayOutputStream
argument_list|()
decl_stmt|;
name|headers
operator|.
name|write
argument_list|(
name|inputHeader
operator|.
name|getBuffer
argument_list|()
argument_list|)
expr_stmt|;
name|headers
operator|.
name|write
argument_list|(
name|inputHeader
operator|.
name|getBuffer
argument_list|()
argument_list|)
expr_stmt|;
name|headers
operator|.
name|write
argument_list|(
name|inputHeader
operator|.
name|getBuffer
argument_list|()
argument_list|)
expr_stmt|;
name|headers
operator|.
name|close
argument_list|()
expr_stmt|;
name|codec
operator|=
operator|new
name|AmqpFrameParser
argument_list|(
operator|new
name|AmqpFrameParser
operator|.
name|AMQPFrameSink
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onFrame
parameter_list|(
name|Object
name|frame
parameter_list|)
block|{
name|frames
operator|.
name|add
argument_list|(
name|frame
argument_list|)
expr_stmt|;
name|codec
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|codec
operator|.
name|parse
argument_list|(
name|headers
operator|.
name|toBuffer
argument_list|()
operator|.
name|toByteBuffer
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|frames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Object
name|header
range|:
name|frames
control|)
block|{
name|assertTrue
argument_list|(
name|header
operator|instanceof
name|AmqpHeader
argument_list|)
expr_stmt|;
name|AmqpHeader
name|outputHeader
init|=
operator|(
name|AmqpHeader
operator|)
name|header
decl_stmt|;
name|assertHeadersEqual
argument_list|(
name|inputHeader
argument_list|,
name|outputHeader
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testResetReadsNextAMQPHeader
parameter_list|()
throws|throws
name|Exception
block|{
name|AmqpHeader
name|inputHeader
init|=
operator|new
name|AmqpHeader
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|3
condition|;
operator|++
name|i
control|)
block|{
name|codec
operator|.
name|parse
argument_list|(
name|inputHeader
operator|.
name|getBuffer
argument_list|()
operator|.
name|toByteBuffer
argument_list|()
argument_list|)
expr_stmt|;
name|codec
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|i
argument_list|,
name|frames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Object
name|outputFrame
init|=
name|frames
operator|.
name|get
argument_list|(
name|i
operator|-
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|outputFrame
operator|instanceof
name|AmqpHeader
argument_list|)
expr_stmt|;
name|AmqpHeader
name|outputHeader
init|=
operator|(
name|AmqpHeader
operator|)
name|outputFrame
decl_stmt|;
name|assertHeadersEqual
argument_list|(
name|inputHeader
argument_list|,
name|outputHeader
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testResetReadsNextAMQPHeaderAfterContentParsed
parameter_list|()
throws|throws
name|Exception
block|{
name|AmqpHeader
name|inputHeader
init|=
operator|new
name|AmqpHeader
argument_list|()
decl_stmt|;
name|byte
index|[]
name|CONTENTS
init|=
operator|new
name|byte
index|[
name|MESSAGE_SIZE
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
name|MESSAGE_SIZE
condition|;
name|i
operator|++
control|)
block|{
name|CONTENTS
index|[
name|i
index|]
operator|=
literal|'a'
expr_stmt|;
block|}
name|DataByteArrayOutputStream
name|output
init|=
operator|new
name|DataByteArrayOutputStream
argument_list|()
decl_stmt|;
name|output
operator|.
name|write
argument_list|(
name|inputHeader
operator|.
name|getBuffer
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|MESSAGE_SIZE
operator|+
literal|4
argument_list|)
expr_stmt|;
name|output
operator|.
name|write
argument_list|(
name|CONTENTS
argument_list|)
expr_stmt|;
name|output
operator|.
name|write
argument_list|(
name|inputHeader
operator|.
name|getBuffer
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|MESSAGE_SIZE
operator|+
literal|4
argument_list|)
expr_stmt|;
name|output
operator|.
name|write
argument_list|(
name|CONTENTS
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|codec
operator|=
operator|new
name|AmqpFrameParser
argument_list|(
operator|new
name|AmqpFrameParser
operator|.
name|AMQPFrameSink
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onFrame
parameter_list|(
name|Object
name|frame
parameter_list|)
block|{
name|frames
operator|.
name|add
argument_list|(
name|frame
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|frame
operator|instanceof
name|AmqpHeader
operator|)
condition|)
block|{
name|codec
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|codec
operator|.
name|parse
argument_list|(
name|output
operator|.
name|toBuffer
argument_list|()
operator|.
name|toByteBuffer
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|4
condition|;
operator|++
name|i
control|)
block|{
name|Object
name|frame
init|=
name|frames
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|frame
operator|instanceof
name|AmqpHeader
argument_list|)
expr_stmt|;
name|AmqpHeader
name|outputHeader
init|=
operator|(
name|AmqpHeader
operator|)
name|frame
decl_stmt|;
name|assertHeadersEqual
argument_list|(
name|inputHeader
argument_list|,
name|outputHeader
argument_list|)
expr_stmt|;
name|frame
operator|=
name|frames
operator|.
name|get
argument_list|(
operator|++
name|i
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|frame
operator|instanceof
name|AmqpHeader
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|frame
operator|instanceof
name|Buffer
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MESSAGE_SIZE
operator|+
literal|4
argument_list|,
operator|(
operator|(
name|Buffer
operator|)
name|frame
operator|)
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHeaderAndFrameAreRead
parameter_list|()
throws|throws
name|Exception
block|{
name|AmqpHeader
name|inputHeader
init|=
operator|new
name|AmqpHeader
argument_list|()
decl_stmt|;
name|DataByteArrayOutputStream
name|output
init|=
operator|new
name|DataByteArrayOutputStream
argument_list|()
decl_stmt|;
name|byte
index|[]
name|CONTENTS
init|=
operator|new
name|byte
index|[
name|MESSAGE_SIZE
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
name|MESSAGE_SIZE
condition|;
name|i
operator|++
control|)
block|{
name|CONTENTS
index|[
name|i
index|]
operator|=
literal|'a'
expr_stmt|;
block|}
name|output
operator|.
name|write
argument_list|(
name|inputHeader
operator|.
name|getBuffer
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|MESSAGE_SIZE
operator|+
literal|4
argument_list|)
expr_stmt|;
name|output
operator|.
name|write
argument_list|(
name|CONTENTS
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|codec
operator|.
name|parse
argument_list|(
name|output
operator|.
name|toBuffer
argument_list|()
operator|.
name|toByteBuffer
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|frames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Object
name|outputFrame
init|=
name|frames
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|outputFrame
operator|instanceof
name|AmqpHeader
argument_list|)
expr_stmt|;
name|AmqpHeader
name|outputHeader
init|=
operator|(
name|AmqpHeader
operator|)
name|outputFrame
decl_stmt|;
name|assertHeadersEqual
argument_list|(
name|inputHeader
argument_list|,
name|outputHeader
argument_list|)
expr_stmt|;
name|outputFrame
operator|=
name|frames
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outputFrame
operator|instanceof
name|Buffer
argument_list|)
expr_stmt|;
name|Buffer
name|frame
init|=
operator|(
name|Buffer
operator|)
name|outputFrame
decl_stmt|;
name|assertEquals
argument_list|(
name|MESSAGE_SIZE
operator|+
literal|4
argument_list|,
name|frame
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHeaderAndFrameAreReadNoWireFormat
parameter_list|()
throws|throws
name|Exception
block|{
name|codec
operator|.
name|setWireFormat
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|AmqpHeader
name|inputHeader
init|=
operator|new
name|AmqpHeader
argument_list|()
decl_stmt|;
name|DataByteArrayOutputStream
name|output
init|=
operator|new
name|DataByteArrayOutputStream
argument_list|()
decl_stmt|;
name|byte
index|[]
name|CONTENTS
init|=
operator|new
name|byte
index|[
name|MESSAGE_SIZE
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
name|MESSAGE_SIZE
condition|;
name|i
operator|++
control|)
block|{
name|CONTENTS
index|[
name|i
index|]
operator|=
literal|'a'
expr_stmt|;
block|}
name|output
operator|.
name|write
argument_list|(
name|inputHeader
operator|.
name|getBuffer
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|MESSAGE_SIZE
operator|+
literal|4
argument_list|)
expr_stmt|;
name|output
operator|.
name|write
argument_list|(
name|CONTENTS
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|codec
operator|.
name|parse
argument_list|(
name|output
operator|.
name|toBuffer
argument_list|()
operator|.
name|toByteBuffer
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|frames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Object
name|outputFrame
init|=
name|frames
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|outputFrame
operator|instanceof
name|AmqpHeader
argument_list|)
expr_stmt|;
name|AmqpHeader
name|outputHeader
init|=
operator|(
name|AmqpHeader
operator|)
name|outputFrame
decl_stmt|;
name|assertHeadersEqual
argument_list|(
name|inputHeader
argument_list|,
name|outputHeader
argument_list|)
expr_stmt|;
name|outputFrame
operator|=
name|frames
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outputFrame
operator|instanceof
name|Buffer
argument_list|)
expr_stmt|;
name|Buffer
name|frame
init|=
operator|(
name|Buffer
operator|)
name|outputFrame
decl_stmt|;
name|assertEquals
argument_list|(
name|MESSAGE_SIZE
operator|+
literal|4
argument_list|,
name|frame
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHeaderAndMulitpleFramesAreRead
parameter_list|()
throws|throws
name|Exception
block|{
name|AmqpHeader
name|inputHeader
init|=
operator|new
name|AmqpHeader
argument_list|()
decl_stmt|;
specifier|final
name|int
name|FRAME_SIZE_HEADER
init|=
literal|4
decl_stmt|;
specifier|final
name|int
name|FRAME_SIZE
init|=
literal|65531
decl_stmt|;
specifier|final
name|int
name|NUM_FRAMES
init|=
literal|5
decl_stmt|;
name|DataByteArrayOutputStream
name|output
init|=
operator|new
name|DataByteArrayOutputStream
argument_list|()
decl_stmt|;
name|byte
index|[]
name|CONTENTS
init|=
operator|new
name|byte
index|[
name|FRAME_SIZE
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
name|FRAME_SIZE
condition|;
name|i
operator|++
control|)
block|{
name|CONTENTS
index|[
name|i
index|]
operator|=
literal|'a'
expr_stmt|;
block|}
name|output
operator|.
name|write
argument_list|(
name|inputHeader
operator|.
name|getBuffer
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_FRAMES
condition|;
operator|++
name|i
control|)
block|{
name|output
operator|.
name|writeInt
argument_list|(
name|FRAME_SIZE
operator|+
name|FRAME_SIZE_HEADER
argument_list|)
expr_stmt|;
name|output
operator|.
name|write
argument_list|(
name|CONTENTS
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|codec
operator|.
name|parse
argument_list|(
name|output
operator|.
name|toBuffer
argument_list|()
operator|.
name|toByteBuffer
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_FRAMES
operator|+
literal|1
argument_list|,
name|frames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Object
name|outputFrame
init|=
name|frames
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|outputFrame
operator|instanceof
name|AmqpHeader
argument_list|)
expr_stmt|;
name|AmqpHeader
name|outputHeader
init|=
operator|(
name|AmqpHeader
operator|)
name|outputFrame
decl_stmt|;
name|assertHeadersEqual
argument_list|(
name|inputHeader
argument_list|,
name|outputHeader
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|NUM_FRAMES
condition|;
operator|++
name|i
control|)
block|{
name|outputFrame
operator|=
name|frames
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outputFrame
operator|instanceof
name|Buffer
argument_list|)
expr_stmt|;
name|Buffer
name|frame
init|=
operator|(
name|Buffer
operator|)
name|outputFrame
decl_stmt|;
name|assertEquals
argument_list|(
name|FRAME_SIZE
operator|+
name|FRAME_SIZE_HEADER
argument_list|,
name|frame
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCodecRejectsToLargeFrames
parameter_list|()
throws|throws
name|Exception
block|{
name|amqpWireFormat
operator|.
name|setMaxFrameSize
argument_list|(
name|MESSAGE_SIZE
argument_list|)
expr_stmt|;
name|AmqpHeader
name|inputHeader
init|=
operator|new
name|AmqpHeader
argument_list|()
decl_stmt|;
name|DataByteArrayOutputStream
name|output
init|=
operator|new
name|DataByteArrayOutputStream
argument_list|()
decl_stmt|;
name|byte
index|[]
name|CONTENTS
init|=
operator|new
name|byte
index|[
name|MESSAGE_SIZE
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
name|MESSAGE_SIZE
condition|;
name|i
operator|++
control|)
block|{
name|CONTENTS
index|[
name|i
index|]
operator|=
literal|'a'
expr_stmt|;
block|}
name|output
operator|.
name|write
argument_list|(
name|inputHeader
operator|.
name|getBuffer
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|MESSAGE_SIZE
operator|+
literal|4
argument_list|)
expr_stmt|;
name|output
operator|.
name|write
argument_list|(
name|CONTENTS
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|codec
operator|.
name|parse
argument_list|(
name|output
operator|.
name|toBuffer
argument_list|()
operator|.
name|toByteBuffer
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have failed to read the large frame."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Caught expected error: {}"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReadPartialPayload
parameter_list|()
throws|throws
name|Exception
block|{
name|AmqpHeader
name|inputHeader
init|=
operator|new
name|AmqpHeader
argument_list|()
decl_stmt|;
name|DataByteArrayOutputStream
name|output
init|=
operator|new
name|DataByteArrayOutputStream
argument_list|()
decl_stmt|;
name|byte
index|[]
name|HALF_CONTENT
init|=
operator|new
name|byte
index|[
name|MESSAGE_SIZE
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
name|MESSAGE_SIZE
operator|/
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|HALF_CONTENT
index|[
name|i
index|]
operator|=
literal|'a'
expr_stmt|;
block|}
name|output
operator|.
name|write
argument_list|(
name|inputHeader
operator|.
name|getBuffer
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|MESSAGE_SIZE
operator|+
literal|4
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|codec
operator|.
name|parse
argument_list|(
name|output
operator|.
name|toBuffer
argument_list|()
operator|.
name|toByteBuffer
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|frames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|=
operator|new
name|DataByteArrayOutputStream
argument_list|()
expr_stmt|;
name|output
operator|.
name|write
argument_list|(
name|HALF_CONTENT
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|codec
operator|.
name|parse
argument_list|(
name|output
operator|.
name|toBuffer
argument_list|()
operator|.
name|toByteBuffer
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|frames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|=
operator|new
name|DataByteArrayOutputStream
argument_list|()
expr_stmt|;
name|output
operator|.
name|write
argument_list|(
name|HALF_CONTENT
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|codec
operator|.
name|parse
argument_list|(
name|output
operator|.
name|toBuffer
argument_list|()
operator|.
name|toByteBuffer
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|frames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertHeadersEqual
parameter_list|(
name|AmqpHeader
name|expected
parameter_list|,
name|AmqpHeader
name|actual
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|expected
operator|.
name|getBuffer
argument_list|()
operator|.
name|equals
argument_list|(
name|actual
operator|.
name|getBuffer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

