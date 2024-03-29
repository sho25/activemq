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
name|store
operator|.
name|kahadb
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|protobuf
operator|.
name|Buffer
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
name|store
operator|.
name|kahadb
operator|.
name|data
operator|.
name|KahaAddMessageCommand
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
name|store
operator|.
name|kahadb
operator|.
name|data
operator|.
name|KahaDestination
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
name|store
operator|.
name|kahadb
operator|.
name|data
operator|.
name|KahaDestination
operator|.
name|DestinationType
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
name|store
operator|.
name|kahadb
operator|.
name|data
operator|.
name|KahaEntryType
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
name|util
operator|.
name|ByteSequence
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
name|util
operator|.
name|DataByteArrayInputStream
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
name|util
operator|.
name|DataByteArrayOutputStream
import|;
end_import

begin_class
specifier|public
class|class
name|PBMesssagesTest
extends|extends
name|TestCase
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
specifier|public
name|void
name|testKahaAddMessageCommand
parameter_list|()
throws|throws
name|IOException
block|{
name|KahaAddMessageCommand
name|expected
init|=
operator|new
name|KahaAddMessageCommand
argument_list|()
decl_stmt|;
name|expected
operator|.
name|setDestination
argument_list|(
operator|new
name|KahaDestination
argument_list|()
operator|.
name|setName
argument_list|(
literal|"Foo"
argument_list|)
operator|.
name|setType
argument_list|(
name|DestinationType
operator|.
name|QUEUE
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|.
name|setMessage
argument_list|(
operator|new
name|Buffer
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|,
literal|6
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|.
name|setMessageId
argument_list|(
literal|"Hello World"
argument_list|)
expr_stmt|;
name|int
name|size
init|=
name|expected
operator|.
name|serializedSizeFramed
argument_list|()
decl_stmt|;
name|DataByteArrayOutputStream
name|os
init|=
operator|new
name|DataByteArrayOutputStream
argument_list|(
name|size
operator|+
literal|1
argument_list|)
decl_stmt|;
name|os
operator|.
name|writeByte
argument_list|(
name|expected
operator|.
name|type
argument_list|()
operator|.
name|getNumber
argument_list|()
argument_list|)
expr_stmt|;
name|expected
operator|.
name|writeFramed
argument_list|(
name|os
argument_list|)
expr_stmt|;
name|ByteSequence
name|seq
init|=
name|os
operator|.
name|toByteSequence
argument_list|()
decl_stmt|;
name|DataByteArrayInputStream
name|is
init|=
operator|new
name|DataByteArrayInputStream
argument_list|(
name|seq
argument_list|)
decl_stmt|;
name|KahaEntryType
name|type
init|=
name|KahaEntryType
operator|.
name|valueOf
argument_list|(
name|is
operator|.
name|readByte
argument_list|()
argument_list|)
decl_stmt|;
name|JournalCommand
name|message
init|=
operator|(
name|JournalCommand
operator|)
name|type
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|mergeFramed
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

