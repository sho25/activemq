begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|assertNotSame
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
name|assertSame
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
name|DataInputStream
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
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
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
name|Arrays
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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

begin_comment
comment|/**  * Tests for the AMQP Transfer Tag Generator  */
end_comment

begin_class
specifier|public
class|class
name|AmqpTransferTagGeneratorTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testCreate
parameter_list|()
block|{
name|AmqpTransferTagGenerator
name|tagGen
init|=
operator|new
name|AmqpTransferTagGenerator
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|tagGen
operator|.
name|isPooling
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|AmqpTransferTagGenerator
operator|.
name|DEFAULT_TAG_POOL_SIZE
argument_list|,
name|tagGen
operator|.
name|getMaxPoolSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateDisabled
parameter_list|()
block|{
name|AmqpTransferTagGenerator
name|tagGen
init|=
operator|new
name|AmqpTransferTagGenerator
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|tagGen
operator|.
name|isPooling
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|AmqpTransferTagGenerator
operator|.
name|DEFAULT_TAG_POOL_SIZE
argument_list|,
name|tagGen
operator|.
name|getMaxPoolSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNewTagsOnSuccessiveCheckouts
parameter_list|()
block|{
name|AmqpTransferTagGenerator
name|tagGen
init|=
operator|new
name|AmqpTransferTagGenerator
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|byte
index|[]
name|tag1
init|=
name|tagGen
operator|.
name|getNextTag
argument_list|()
decl_stmt|;
name|byte
index|[]
name|tag2
init|=
name|tagGen
operator|.
name|getNextTag
argument_list|()
decl_stmt|;
name|byte
index|[]
name|tag3
init|=
name|tagGen
operator|.
name|getNextTag
argument_list|()
decl_stmt|;
name|assertNotSame
argument_list|(
name|tag1
argument_list|,
name|tag2
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|tag1
argument_list|,
name|tag3
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|tag3
argument_list|,
name|tag2
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|tag1
argument_list|,
name|tag2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|tag1
argument_list|,
name|tag3
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|tag3
argument_list|,
name|tag2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTagPoolingInEffect
parameter_list|()
block|{
name|AmqpTransferTagGenerator
name|tagGen
init|=
operator|new
name|AmqpTransferTagGenerator
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|byte
index|[]
name|tag1
init|=
name|tagGen
operator|.
name|getNextTag
argument_list|()
decl_stmt|;
name|byte
index|[]
name|tag2
init|=
name|tagGen
operator|.
name|getNextTag
argument_list|()
decl_stmt|;
name|tagGen
operator|.
name|returnTag
argument_list|(
name|tag1
argument_list|)
expr_stmt|;
name|tagGen
operator|.
name|returnTag
argument_list|(
name|tag2
argument_list|)
expr_stmt|;
name|byte
index|[]
name|tag3
init|=
name|tagGen
operator|.
name|getNextTag
argument_list|()
decl_stmt|;
name|byte
index|[]
name|tag4
init|=
name|tagGen
operator|.
name|getNextTag
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|tag1
argument_list|,
name|tag3
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|tag2
argument_list|,
name|tag4
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|tag1
argument_list|,
name|tag4
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|tag2
argument_list|,
name|tag3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPooledTagsReturnedInCheckedInOrder
parameter_list|()
block|{
name|AmqpTransferTagGenerator
name|tagGen
init|=
operator|new
name|AmqpTransferTagGenerator
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|byte
index|[]
name|tag1
init|=
name|tagGen
operator|.
name|getNextTag
argument_list|()
decl_stmt|;
name|byte
index|[]
name|tag2
init|=
name|tagGen
operator|.
name|getNextTag
argument_list|()
decl_stmt|;
name|tagGen
operator|.
name|returnTag
argument_list|(
name|tag2
argument_list|)
expr_stmt|;
name|tagGen
operator|.
name|returnTag
argument_list|(
name|tag1
argument_list|)
expr_stmt|;
name|byte
index|[]
name|tag3
init|=
name|tagGen
operator|.
name|getNextTag
argument_list|()
decl_stmt|;
name|byte
index|[]
name|tag4
init|=
name|tagGen
operator|.
name|getNextTag
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|tag1
argument_list|,
name|tag4
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|tag2
argument_list|,
name|tag3
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|tag1
argument_list|,
name|tag3
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|tag2
argument_list|,
name|tag4
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTagArrayGrowsWithTagValue
parameter_list|()
block|{
name|AmqpTransferTagGenerator
name|tagGen
init|=
operator|new
name|AmqpTransferTagGenerator
argument_list|(
literal|false
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
literal|512
condition|;
operator|++
name|i
control|)
block|{
name|byte
index|[]
name|tag
init|=
name|tagGen
operator|.
name|getNextTag
argument_list|()
decl_stmt|;
if|if
condition|(
name|i
operator|<
literal|256
condition|)
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tag
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|tag
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTagValueMatchesParsedArray
parameter_list|()
throws|throws
name|IOException
block|{
name|AmqpTransferTagGenerator
name|tagGen
init|=
operator|new
name|AmqpTransferTagGenerator
argument_list|(
literal|false
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
name|Short
operator|.
name|MAX_VALUE
condition|;
operator|++
name|i
control|)
block|{
name|byte
index|[]
name|tag
init|=
name|tagGen
operator|.
name|getNextTag
argument_list|()
decl_stmt|;
name|ByteArrayInputStream
name|bais
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|tag
argument_list|)
decl_stmt|;
name|DataInputStream
name|dis
init|=
operator|new
name|DataInputStream
argument_list|(
name|bais
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|<
literal|256
condition|)
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tag
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|,
name|dis
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|tag
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|i
argument_list|,
name|dis
operator|.
name|readShort
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTagGenerationWorksWithIdRollover
parameter_list|()
throws|throws
name|Exception
block|{
name|AmqpTransferTagGenerator
name|tagGen
init|=
operator|new
name|AmqpTransferTagGenerator
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|Field
name|urisField
init|=
name|tagGen
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredField
argument_list|(
literal|"nextTagId"
argument_list|)
decl_stmt|;
name|urisField
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|urisField
operator|.
name|set
argument_list|(
name|tagGen
argument_list|,
name|Long
operator|.
name|MAX_VALUE
operator|+
literal|1
argument_list|)
expr_stmt|;
block|{
name|byte
index|[]
name|tag
init|=
name|tagGen
operator|.
name|getNextTag
argument_list|()
decl_stmt|;
name|ByteArrayInputStream
name|bais
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|tag
argument_list|)
decl_stmt|;
name|DataInputStream
name|dis
init|=
operator|new
name|DataInputStream
argument_list|(
name|bais
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|tag
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Long
operator|.
name|MAX_VALUE
operator|+
literal|1
argument_list|,
name|dis
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|{
name|byte
index|[]
name|tag
init|=
name|tagGen
operator|.
name|getNextTag
argument_list|()
decl_stmt|;
name|ByteArrayInputStream
name|bais
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|tag
argument_list|)
decl_stmt|;
name|DataInputStream
name|dis
init|=
operator|new
name|DataInputStream
argument_list|(
name|bais
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|tag
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Long
operator|.
name|MAX_VALUE
operator|+
literal|2
argument_list|,
name|dis
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Ignore
argument_list|(
literal|"Used to test performance"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testTagGeneratorOverTime
parameter_list|()
block|{
specifier|final
name|AmqpTransferTagGenerator
name|tagGen
init|=
operator|new
name|AmqpTransferTagGenerator
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|int
name|tagLoop
init|=
name|AmqpTransferTagGenerator
operator|.
name|DEFAULT_TAG_POOL_SIZE
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|byte
index|[]
argument_list|>
name|tags
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|tagLoop
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
name|Short
operator|.
name|MAX_VALUE
operator|*
literal|16
condition|;
operator|++
name|i
control|)
block|{
comment|// Checkout all the tags the pool will create
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|tagLoop
condition|;
operator|++
name|j
control|)
block|{
name|tags
operator|.
name|add
argument_list|(
name|tagGen
operator|.
name|getNextTag
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Return them and then clear
name|tags
operator|.
name|forEach
argument_list|(
parameter_list|(
name|tag
parameter_list|)
lambda|->
name|tagGen
operator|.
name|returnTag
argument_list|(
name|tag
argument_list|)
argument_list|)
expr_stmt|;
name|tags
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

