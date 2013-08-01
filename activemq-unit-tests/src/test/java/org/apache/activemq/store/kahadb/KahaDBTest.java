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
name|File
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
name|io
operator|.
name|RandomAccessFile
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
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
name|ActiveMQConnectionFactory
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
name|broker
operator|.
name|BrokerService
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
name|ActiveMQQueue
import|;
end_import

begin_comment
comment|/**  * @author chirino  */
end_comment

begin_class
specifier|public
class|class
name|KahaDBTest
extends|extends
name|TestCase
block|{
specifier|protected
name|BrokerService
name|createBroker
parameter_list|(
name|KahaDBStore
name|kaha
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|kaha
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|private
name|KahaDBStore
name|createStore
parameter_list|(
name|boolean
name|delete
parameter_list|)
throws|throws
name|IOException
block|{
name|KahaDBStore
name|kaha
init|=
operator|new
name|KahaDBStore
argument_list|()
decl_stmt|;
name|kaha
operator|.
name|setDirectory
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/activemq-data/kahadb"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|delete
condition|)
block|{
name|kaha
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
block|}
return|return
name|kaha
return|;
block|}
specifier|public
name|void
name|testIgnoreMissingJournalfilesOptionSetFalse
parameter_list|()
throws|throws
name|Exception
block|{
name|KahaDBStore
name|kaha
init|=
name|createStore
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|kaha
operator|.
name|setJournalMaxFileLength
argument_list|(
literal|1024
operator|*
literal|100
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|kaha
operator|.
name|isIgnoreMissingJournalfiles
argument_list|()
argument_list|)
expr_stmt|;
name|BrokerService
name|broker
init|=
name|createBroker
argument_list|(
name|kaha
argument_list|)
decl_stmt|;
name|sendMessages
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|// Delete some journal files..
name|assertExistsAndDelete
argument_list|(
operator|new
name|File
argument_list|(
name|kaha
operator|.
name|getDirectory
argument_list|()
argument_list|,
literal|"db-4.log"
argument_list|)
argument_list|)
expr_stmt|;
name|assertExistsAndDelete
argument_list|(
operator|new
name|File
argument_list|(
name|kaha
operator|.
name|getDirectory
argument_list|()
argument_list|,
literal|"db-8.log"
argument_list|)
argument_list|)
expr_stmt|;
name|kaha
operator|=
name|createStore
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|kaha
operator|.
name|setJournalMaxFileLength
argument_list|(
literal|1024
operator|*
literal|100
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|kaha
operator|.
name|isIgnoreMissingJournalfiles
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|broker
operator|=
name|createBroker
argument_list|(
name|kaha
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected IOException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Detected missing/corrupt journal files"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testIgnoreMissingJournalfilesOptionSetTrue
parameter_list|()
throws|throws
name|Exception
block|{
name|KahaDBStore
name|kaha
init|=
name|createStore
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|kaha
operator|.
name|setJournalMaxFileLength
argument_list|(
literal|1024
operator|*
literal|100
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|kaha
operator|.
name|isIgnoreMissingJournalfiles
argument_list|()
argument_list|)
expr_stmt|;
name|BrokerService
name|broker
init|=
name|createBroker
argument_list|(
name|kaha
argument_list|)
decl_stmt|;
name|sendMessages
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|// Delete some journal files..
name|assertExistsAndDelete
argument_list|(
operator|new
name|File
argument_list|(
name|kaha
operator|.
name|getDirectory
argument_list|()
argument_list|,
literal|"db-4.log"
argument_list|)
argument_list|)
expr_stmt|;
name|assertExistsAndDelete
argument_list|(
operator|new
name|File
argument_list|(
name|kaha
operator|.
name|getDirectory
argument_list|()
argument_list|,
literal|"db-8.log"
argument_list|)
argument_list|)
expr_stmt|;
name|kaha
operator|=
name|createStore
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|kaha
operator|.
name|setIgnoreMissingJournalfiles
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|kaha
operator|.
name|setJournalMaxFileLength
argument_list|(
literal|1024
operator|*
literal|100
argument_list|)
expr_stmt|;
name|broker
operator|=
name|createBroker
argument_list|(
name|kaha
argument_list|)
expr_stmt|;
comment|// We know we won't get all the messages but we should get most of them.
name|int
name|count
init|=
name|receiveMessages
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|count
operator|>
literal|800
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|count
operator|<
literal|1000
argument_list|)
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testCheckCorruptionNotIgnored
parameter_list|()
throws|throws
name|Exception
block|{
name|KahaDBStore
name|kaha
init|=
name|createStore
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|kaha
operator|.
name|isChecksumJournalFiles
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|kaha
operator|.
name|isCheckForCorruptJournalFiles
argument_list|()
argument_list|)
expr_stmt|;
name|kaha
operator|.
name|setJournalMaxFileLength
argument_list|(
literal|1024
operator|*
literal|100
argument_list|)
expr_stmt|;
name|kaha
operator|.
name|setChecksumJournalFiles
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|BrokerService
name|broker
init|=
name|createBroker
argument_list|(
name|kaha
argument_list|)
decl_stmt|;
name|sendMessages
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|// Modify/Corrupt some journal files..
name|assertExistsAndCorrupt
argument_list|(
operator|new
name|File
argument_list|(
name|kaha
operator|.
name|getDirectory
argument_list|()
argument_list|,
literal|"db-4.log"
argument_list|)
argument_list|)
expr_stmt|;
name|assertExistsAndCorrupt
argument_list|(
operator|new
name|File
argument_list|(
name|kaha
operator|.
name|getDirectory
argument_list|()
argument_list|,
literal|"db-8.log"
argument_list|)
argument_list|)
expr_stmt|;
name|kaha
operator|=
name|createStore
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|kaha
operator|.
name|setJournalMaxFileLength
argument_list|(
literal|1024
operator|*
literal|100
argument_list|)
expr_stmt|;
name|kaha
operator|.
name|setChecksumJournalFiles
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|kaha
operator|.
name|setCheckForCorruptJournalFiles
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|kaha
operator|.
name|isIgnoreMissingJournalfiles
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|broker
operator|=
name|createBroker
argument_list|(
name|kaha
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected IOException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Detected missing/corrupt journal files"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testMigrationOnNewDefaultForChecksumJournalFiles
parameter_list|()
throws|throws
name|Exception
block|{
name|KahaDBStore
name|kaha
init|=
name|createStore
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|kaha
operator|.
name|setChecksumJournalFiles
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|kaha
operator|.
name|isChecksumJournalFiles
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|kaha
operator|.
name|isCheckForCorruptJournalFiles
argument_list|()
argument_list|)
expr_stmt|;
name|kaha
operator|.
name|setJournalMaxFileLength
argument_list|(
literal|1024
operator|*
literal|100
argument_list|)
expr_stmt|;
name|BrokerService
name|broker
init|=
name|createBroker
argument_list|(
name|kaha
argument_list|)
decl_stmt|;
name|sendMessages
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|kaha
operator|=
name|createStore
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|kaha
operator|.
name|setJournalMaxFileLength
argument_list|(
literal|1024
operator|*
literal|100
argument_list|)
expr_stmt|;
name|kaha
operator|.
name|setCheckForCorruptJournalFiles
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|kaha
operator|.
name|isIgnoreMissingJournalfiles
argument_list|()
argument_list|)
expr_stmt|;
name|createBroker
argument_list|(
name|kaha
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1000
argument_list|,
name|receiveMessages
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertExistsAndCorrupt
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|assertTrue
argument_list|(
name|file
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|RandomAccessFile
name|f
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|file
argument_list|,
literal|"rw"
argument_list|)
decl_stmt|;
try|try
block|{
name|f
operator|.
name|seek
argument_list|(
literal|1024
operator|*
literal|5
operator|+
literal|134
argument_list|)
expr_stmt|;
name|f
operator|.
name|write
argument_list|(
literal|"... corruption string ..."
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|f
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testCheckCorruptionIgnored
parameter_list|()
throws|throws
name|Exception
block|{
name|KahaDBStore
name|kaha
init|=
name|createStore
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|kaha
operator|.
name|setJournalMaxFileLength
argument_list|(
literal|1024
operator|*
literal|100
argument_list|)
expr_stmt|;
name|BrokerService
name|broker
init|=
name|createBroker
argument_list|(
name|kaha
argument_list|)
decl_stmt|;
name|sendMessages
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|// Delete some journal files..
name|assertExistsAndCorrupt
argument_list|(
operator|new
name|File
argument_list|(
name|kaha
operator|.
name|getDirectory
argument_list|()
argument_list|,
literal|"db-4.log"
argument_list|)
argument_list|)
expr_stmt|;
name|assertExistsAndCorrupt
argument_list|(
operator|new
name|File
argument_list|(
name|kaha
operator|.
name|getDirectory
argument_list|()
argument_list|,
literal|"db-8.log"
argument_list|)
argument_list|)
expr_stmt|;
name|kaha
operator|=
name|createStore
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|kaha
operator|.
name|setIgnoreMissingJournalfiles
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|kaha
operator|.
name|setJournalMaxFileLength
argument_list|(
literal|1024
operator|*
literal|100
argument_list|)
expr_stmt|;
name|kaha
operator|.
name|setCheckForCorruptJournalFiles
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|=
name|createBroker
argument_list|(
name|kaha
argument_list|)
expr_stmt|;
comment|// We know we won't get all the messages but we should get most of them.
name|int
name|count
init|=
name|receiveMessages
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Expected to received a min # of messages.. Got: "
operator|+
name|count
argument_list|,
name|count
operator|>
literal|990
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|count
operator|<
literal|1000
argument_list|)
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|assertExistsAndDelete
parameter_list|(
name|File
name|file
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|file
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|file
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|sendMessages
parameter_list|(
name|int
name|count
parameter_list|)
throws|throws
name|JMSException
block|{
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|Session
name|session
init|=
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
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST"
argument_list|)
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
name|createContent
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|int
name|receiveMessages
parameter_list|()
throws|throws
name|JMSException
block|{
name|int
name|rc
init|=
literal|0
decl_stmt|;
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
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
name|MessageConsumer
name|messageConsumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST"
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
name|messageConsumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|rc
operator|++
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
finally|finally
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|String
name|createContent
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|i
operator|+
literal|":"
argument_list|)
decl_stmt|;
while|while
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|<
literal|1024
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"*"
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

