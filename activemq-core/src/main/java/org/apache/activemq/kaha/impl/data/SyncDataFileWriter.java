begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|data
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
name|java
operator|.
name|io
operator|.
name|RandomAccessFile
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
name|kaha
operator|.
name|Marshaller
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

begin_comment
comment|/**  * Optimized Store writer.  Synchronously marshalls and writes to the data file. Simple but   * may introduce a bit of contention when put under load.  *   * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|final
specifier|public
class|class
name|SyncDataFileWriter
block|{
specifier|private
name|DataByteArrayOutputStream
name|buffer
decl_stmt|;
specifier|private
name|DataManagerImpl
name|dataManager
decl_stmt|;
comment|/**      * Construct a Store writer      *       * @param file      */
name|SyncDataFileWriter
parameter_list|(
name|DataManagerImpl
name|fileManager
parameter_list|)
block|{
name|this
operator|.
name|dataManager
operator|=
name|fileManager
expr_stmt|;
name|this
operator|.
name|buffer
operator|=
operator|new
name|DataByteArrayOutputStream
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.activemq.kaha.impl.data.DataFileWriter#storeItem(org.apache.activemq.kaha.Marshaller, java.lang.Object, byte) 	 */
specifier|public
specifier|synchronized
name|DataItem
name|storeItem
parameter_list|(
name|Marshaller
name|marshaller
parameter_list|,
name|Object
name|payload
parameter_list|,
name|byte
name|type
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Write the packet our internal buffer.
name|buffer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|buffer
operator|.
name|position
argument_list|(
name|DataManagerImpl
operator|.
name|ITEM_HEAD_SIZE
argument_list|)
expr_stmt|;
name|marshaller
operator|.
name|writePayload
argument_list|(
name|payload
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
name|int
name|size
init|=
name|buffer
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|payloadSize
init|=
name|size
operator|-
name|DataManagerImpl
operator|.
name|ITEM_HEAD_SIZE
decl_stmt|;
name|buffer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|buffer
operator|.
name|writeByte
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|writeInt
argument_list|(
name|payloadSize
argument_list|)
expr_stmt|;
comment|// Find the position where this item will land at.
name|DataItem
name|item
init|=
operator|new
name|DataItem
argument_list|()
decl_stmt|;
name|item
operator|.
name|setSize
argument_list|(
name|payloadSize
argument_list|)
expr_stmt|;
name|DataFile
name|dataFile
init|=
name|dataManager
operator|.
name|findSpaceForData
argument_list|(
name|item
argument_list|)
decl_stmt|;
comment|// Now splat the buffer to the file.
name|dataFile
operator|.
name|getRandomAccessFile
argument_list|()
operator|.
name|seek
argument_list|(
name|item
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|dataFile
operator|.
name|getRandomAccessFile
argument_list|()
operator|.
name|write
argument_list|(
name|buffer
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|dataFile
operator|.
name|setWriterData
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
comment|// Use as dirty marker..
name|dataManager
operator|.
name|addInterestInFile
argument_list|(
name|dataFile
argument_list|)
expr_stmt|;
return|return
name|item
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.activemq.kaha.impl.data.DataFileWriter#updateItem(org.apache.activemq.kaha.StoreLocation, org.apache.activemq.kaha.Marshaller, java.lang.Object, byte) 	 */
specifier|public
specifier|synchronized
name|void
name|updateItem
parameter_list|(
name|DataItem
name|item
parameter_list|,
name|Marshaller
name|marshaller
parameter_list|,
name|Object
name|payload
parameter_list|,
name|byte
name|type
parameter_list|)
throws|throws
name|IOException
block|{
comment|//Write the packet our internal buffer.
name|buffer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|buffer
operator|.
name|position
argument_list|(
name|DataManagerImpl
operator|.
name|ITEM_HEAD_SIZE
argument_list|)
expr_stmt|;
name|marshaller
operator|.
name|writePayload
argument_list|(
name|payload
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
name|int
name|size
init|=
name|buffer
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|payloadSize
init|=
name|size
operator|-
name|DataManagerImpl
operator|.
name|ITEM_HEAD_SIZE
decl_stmt|;
name|buffer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|buffer
operator|.
name|writeByte
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|writeInt
argument_list|(
name|payloadSize
argument_list|)
expr_stmt|;
name|item
operator|.
name|setSize
argument_list|(
name|payloadSize
argument_list|)
expr_stmt|;
name|DataFile
name|dataFile
init|=
name|dataManager
operator|.
name|getDataFile
argument_list|(
name|item
argument_list|)
decl_stmt|;
name|RandomAccessFile
name|file
init|=
name|dataFile
operator|.
name|getRandomAccessFile
argument_list|()
decl_stmt|;
name|file
operator|.
name|seek
argument_list|(
name|item
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|file
operator|.
name|write
argument_list|(
name|buffer
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|dataFile
operator|.
name|setWriterData
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
comment|// Use as dirty marker..
block|}
specifier|public
specifier|synchronized
name|void
name|force
parameter_list|(
name|DataFile
name|dataFile
parameter_list|)
throws|throws
name|IOException
block|{
comment|// If our dirty marker was set.. then we need to sync
if|if
condition|(
name|dataFile
operator|.
name|getWriterData
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|dataFile
operator|.
name|getRandomAccessFile
argument_list|()
operator|.
name|getFD
argument_list|()
operator|.
name|sync
argument_list|()
expr_stmt|;
name|dataFile
operator|.
name|setWriterData
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{ 	}
block|}
end_class

end_unit

