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
name|kahadb
operator|.
name|journal
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|journal
operator|.
name|DataFileAppender
operator|.
name|WriteCommand
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|journal
operator|.
name|DataFileAppender
operator|.
name|WriteKey
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|util
operator|.
name|ByteSequence
import|;
end_import

begin_comment
comment|/**  * Optimized Store reader and updater. Single threaded and synchronous. Use in  * conjunction with the DataFileAccessorPool of concurrent use.  *   * @version $Revision$  */
end_comment

begin_class
specifier|final
class|class
name|DataFileAccessor
block|{
specifier|private
specifier|final
name|DataFile
name|dataFile
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|WriteKey
argument_list|,
name|WriteCommand
argument_list|>
name|inflightWrites
decl_stmt|;
specifier|private
specifier|final
name|RandomAccessFile
name|file
decl_stmt|;
specifier|private
name|boolean
name|disposed
decl_stmt|;
comment|/**      * Construct a Store reader      *       * @param fileId      * @throws IOException      */
specifier|public
name|DataFileAccessor
parameter_list|(
name|Journal
name|dataManager
parameter_list|,
name|DataFile
name|dataFile
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|dataFile
operator|=
name|dataFile
expr_stmt|;
name|this
operator|.
name|inflightWrites
operator|=
name|dataManager
operator|.
name|getInflightWrites
argument_list|()
expr_stmt|;
name|this
operator|.
name|file
operator|=
name|dataFile
operator|.
name|openRandomAccessFile
argument_list|()
expr_stmt|;
block|}
specifier|public
name|DataFile
name|getDataFile
parameter_list|()
block|{
return|return
name|dataFile
return|;
block|}
specifier|public
name|void
name|dispose
parameter_list|()
block|{
if|if
condition|(
name|disposed
condition|)
block|{
return|return;
block|}
name|disposed
operator|=
literal|true
expr_stmt|;
try|try
block|{
name|dataFile
operator|.
name|closeRandomAccessFile
argument_list|(
name|file
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
block|}
block|}
specifier|public
name|ByteSequence
name|readRecord
parameter_list|(
name|Location
name|location
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|location
operator|.
name|isValid
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid location: "
operator|+
name|location
argument_list|)
throw|;
block|}
name|WriteCommand
name|asyncWrite
init|=
operator|(
name|WriteCommand
operator|)
name|inflightWrites
operator|.
name|get
argument_list|(
operator|new
name|WriteKey
argument_list|(
name|location
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|asyncWrite
operator|!=
literal|null
condition|)
block|{
return|return
name|asyncWrite
operator|.
name|data
return|;
block|}
try|try
block|{
if|if
condition|(
name|location
operator|.
name|getSize
argument_list|()
operator|==
name|Location
operator|.
name|NOT_SET
condition|)
block|{
name|file
operator|.
name|seek
argument_list|(
name|location
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|location
operator|.
name|setSize
argument_list|(
name|file
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
name|location
operator|.
name|setType
argument_list|(
name|file
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|file
operator|.
name|seek
argument_list|(
name|location
operator|.
name|getOffset
argument_list|()
operator|+
name|Journal
operator|.
name|RECORD_HEAD_SPACE
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|location
operator|.
name|getSize
argument_list|()
operator|-
name|Journal
operator|.
name|RECORD_HEAD_SPACE
index|]
decl_stmt|;
name|file
operator|.
name|readFully
argument_list|(
name|data
argument_list|)
expr_stmt|;
return|return
operator|new
name|ByteSequence
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid location: "
operator|+
name|location
operator|+
literal|", : "
operator|+
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|read
parameter_list|(
name|long
name|offset
parameter_list|,
name|byte
name|data
index|[]
parameter_list|)
throws|throws
name|IOException
block|{
name|file
operator|.
name|seek
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|file
operator|.
name|readFully
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|readLocationDetails
parameter_list|(
name|Location
name|location
parameter_list|)
throws|throws
name|IOException
block|{
name|WriteCommand
name|asyncWrite
init|=
operator|(
name|WriteCommand
operator|)
name|inflightWrites
operator|.
name|get
argument_list|(
operator|new
name|WriteKey
argument_list|(
name|location
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|asyncWrite
operator|!=
literal|null
condition|)
block|{
name|location
operator|.
name|setSize
argument_list|(
name|asyncWrite
operator|.
name|location
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|location
operator|.
name|setType
argument_list|(
name|asyncWrite
operator|.
name|location
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|file
operator|.
name|seek
argument_list|(
name|location
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|location
operator|.
name|setSize
argument_list|(
name|file
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
name|location
operator|.
name|setType
argument_list|(
name|file
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|//    public boolean readLocationDetailsAndValidate(Location location) {
comment|//        try {
comment|//            WriteCommand asyncWrite = (WriteCommand)inflightWrites.get(new WriteKey(location));
comment|//            if (asyncWrite != null) {
comment|//                location.setSize(asyncWrite.location.getSize());
comment|//                location.setType(asyncWrite.location.getType());
comment|//            } else {
comment|//                file.seek(location.getOffset());
comment|//                location.setSize(file.readInt());
comment|//                location.setType(file.readByte());
comment|//
comment|//                byte data[] = new byte[3];
comment|//                file.seek(location.getOffset() + Journal.ITEM_HEAD_OFFSET_TO_SOR);
comment|//                file.readFully(data);
comment|//                if (data[0] != Journal.ITEM_HEAD_SOR[0]
comment|//                    || data[1] != Journal.ITEM_HEAD_SOR[1]
comment|//                    || data[2] != Journal.ITEM_HEAD_SOR[2]) {
comment|//                    return false;
comment|//                }
comment|//                file.seek(location.getOffset() + location.getSize() - Journal.ITEM_FOOT_SPACE);
comment|//                file.readFully(data);
comment|//                if (data[0] != Journal.ITEM_HEAD_EOR[0]
comment|//                    || data[1] != Journal.ITEM_HEAD_EOR[1]
comment|//                    || data[2] != Journal.ITEM_HEAD_EOR[2]) {
comment|//                    return false;
comment|//                }
comment|//            }
comment|//        } catch (IOException e) {
comment|//            return false;
comment|//        }
comment|//        return true;
comment|//    }
specifier|public
name|void
name|updateRecord
parameter_list|(
name|Location
name|location
parameter_list|,
name|ByteSequence
name|data
parameter_list|,
name|boolean
name|sync
parameter_list|)
throws|throws
name|IOException
block|{
name|file
operator|.
name|seek
argument_list|(
name|location
operator|.
name|getOffset
argument_list|()
operator|+
name|Journal
operator|.
name|RECORD_HEAD_SPACE
argument_list|)
expr_stmt|;
name|int
name|size
init|=
name|Math
operator|.
name|min
argument_list|(
name|data
operator|.
name|getLength
argument_list|()
argument_list|,
name|location
operator|.
name|getSize
argument_list|()
argument_list|)
decl_stmt|;
name|file
operator|.
name|write
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
name|size
argument_list|)
expr_stmt|;
if|if
condition|(
name|sync
condition|)
block|{
name|file
operator|.
name|getFD
argument_list|()
operator|.
name|sync
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

