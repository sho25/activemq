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
name|blob
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageConsumer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|ActiveMQSession
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
name|BlobMessage
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
name|command
operator|.
name|ActiveMQBlobMessage
import|;
end_import

begin_class
specifier|public
class|class
name|FTPBlobTest
extends|extends
name|FTPTestSupport
block|{
specifier|public
name|void
name|testBlobFile
parameter_list|()
throws|throws
name|Exception
block|{
name|setConnection
argument_list|()
expr_stmt|;
comment|// first create Message
name|File
name|file
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"amq-data-file-"
argument_list|,
literal|".dat"
argument_list|)
decl_stmt|;
comment|// lets write some data
name|String
name|content
init|=
literal|"hello world "
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|BufferedWriter
name|writer
init|=
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
name|file
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|append
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|ActiveMQSession
name|session
init|=
operator|(
name|ActiveMQSession
operator|)
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|BlobMessage
name|message
init|=
name|session
operator|.
name|createBlobMessage
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|message
operator|.
name|setName
argument_list|(
literal|"fileName"
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// check message send
name|Message
name|msg
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|msg
operator|instanceof
name|ActiveMQBlobMessage
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"name is correct"
argument_list|,
literal|"fileName"
argument_list|,
operator|(
operator|(
name|ActiveMQBlobMessage
operator|)
name|msg
operator|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|InputStream
name|input
init|=
operator|(
operator|(
name|ActiveMQBlobMessage
operator|)
name|msg
operator|)
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|i
init|=
name|input
operator|.
name|read
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|!=
operator|-
literal|1
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|i
argument_list|)
expr_stmt|;
name|i
operator|=
name|input
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
name|File
name|uploaded
init|=
operator|new
name|File
argument_list|(
name|ftpHomeDirFile
argument_list|,
name|msg
operator|.
name|getJMSMessageID
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|replace
argument_list|(
literal|":"
argument_list|,
literal|"_"
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|content
argument_list|,
name|b
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|uploaded
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
operator|(
operator|(
name|ActiveMQBlobMessage
operator|)
name|msg
operator|)
operator|.
name|deleteFile
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|uploaded
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

