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
name|kaha
operator|.
name|impl
operator|.
name|async
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
name|apache
operator|.
name|activeio
operator|.
name|journal
operator|.
name|InvalidRecordLocationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|journal
operator|.
name|Journal
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|journal
operator|.
name|JournalEventListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|journal
operator|.
name|RecordLocation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
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
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|packet
operator|.
name|Packet
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

begin_comment
comment|/**  * Provides a Journal Facade to the DataManager.  *   * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|JournalFacade
implements|implements
name|Journal
block|{
specifier|public
specifier|static
class|class
name|RecordLocationFacade
implements|implements
name|RecordLocation
block|{
specifier|private
specifier|final
name|Location
name|location
decl_stmt|;
specifier|public
name|RecordLocationFacade
parameter_list|(
name|Location
name|location
parameter_list|)
block|{
name|this
operator|.
name|location
operator|=
name|location
expr_stmt|;
block|}
specifier|public
name|Location
name|getLocation
parameter_list|()
block|{
return|return
name|location
return|;
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|RecordLocationFacade
name|rlf
init|=
operator|(
name|RecordLocationFacade
operator|)
name|o
decl_stmt|;
name|int
name|rc
init|=
name|location
operator|.
name|compareTo
argument_list|(
name|rlf
operator|.
name|location
argument_list|)
decl_stmt|;
return|return
name|rc
return|;
block|}
block|}
specifier|static
specifier|private
name|RecordLocation
name|convertToRecordLocation
parameter_list|(
name|Location
name|location
parameter_list|)
block|{
if|if
condition|(
name|location
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
operator|new
name|RecordLocationFacade
argument_list|(
name|location
argument_list|)
return|;
block|}
specifier|static
specifier|private
name|Location
name|convertFromRecordLocation
parameter_list|(
name|RecordLocation
name|location
parameter_list|)
block|{
if|if
condition|(
name|location
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
operator|(
operator|(
name|RecordLocationFacade
operator|)
name|location
operator|)
operator|.
name|getLocation
argument_list|()
return|;
block|}
name|AsyncDataManager
name|dataManager
decl_stmt|;
specifier|public
name|JournalFacade
parameter_list|(
name|AsyncDataManager
name|dataManager
parameter_list|)
block|{
name|this
operator|.
name|dataManager
operator|=
name|dataManager
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|dataManager
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|RecordLocation
name|getMark
parameter_list|()
throws|throws
name|IllegalStateException
block|{
return|return
name|convertToRecordLocation
argument_list|(
name|dataManager
operator|.
name|getMark
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|RecordLocation
name|getNextRecordLocation
parameter_list|(
name|RecordLocation
name|location
parameter_list|)
throws|throws
name|InvalidRecordLocationException
throws|,
name|IOException
throws|,
name|IllegalStateException
block|{
return|return
name|convertToRecordLocation
argument_list|(
name|dataManager
operator|.
name|getNextLocation
argument_list|(
name|convertFromRecordLocation
argument_list|(
name|location
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Packet
name|read
parameter_list|(
name|RecordLocation
name|location
parameter_list|)
throws|throws
name|InvalidRecordLocationException
throws|,
name|IOException
throws|,
name|IllegalStateException
block|{
name|ByteSequence
name|rc
init|=
name|dataManager
operator|.
name|read
argument_list|(
name|convertFromRecordLocation
argument_list|(
name|location
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|rc
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
operator|new
name|ByteArrayPacket
argument_list|(
name|rc
operator|.
name|getData
argument_list|()
argument_list|,
name|rc
operator|.
name|getOffset
argument_list|()
argument_list|,
name|rc
operator|.
name|getLength
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|void
name|setJournalEventListener
parameter_list|(
name|JournalEventListener
name|listener
parameter_list|)
throws|throws
name|IllegalStateException
block|{     }
specifier|public
name|void
name|setMark
parameter_list|(
name|RecordLocation
name|location
parameter_list|,
name|boolean
name|sync
parameter_list|)
throws|throws
name|InvalidRecordLocationException
throws|,
name|IOException
throws|,
name|IllegalStateException
block|{
name|dataManager
operator|.
name|setMark
argument_list|(
name|convertFromRecordLocation
argument_list|(
name|location
argument_list|)
argument_list|,
name|sync
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RecordLocation
name|write
parameter_list|(
name|Packet
name|packet
parameter_list|,
name|boolean
name|sync
parameter_list|)
throws|throws
name|IOException
throws|,
name|IllegalStateException
block|{
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|packet
operator|.
name|ByteSequence
name|data
init|=
name|packet
operator|.
name|asByteSequence
argument_list|()
decl_stmt|;
name|ByteSequence
name|sequence
init|=
operator|new
name|ByteSequence
argument_list|(
name|data
operator|.
name|getData
argument_list|()
argument_list|,
name|data
operator|.
name|getOffset
argument_list|()
argument_list|,
name|data
operator|.
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|convertToRecordLocation
argument_list|(
name|dataManager
operator|.
name|write
argument_list|(
name|sequence
argument_list|,
name|sync
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

