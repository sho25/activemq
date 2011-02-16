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
name|index
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|kaha
operator|.
name|StoreEntry
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
name|StoreLocation
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
name|impl
operator|.
name|data
operator|.
name|DataItem
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
name|impl
operator|.
name|data
operator|.
name|Item
import|;
end_import

begin_comment
comment|/**  * A an Item with a relative position and location to other Items in the Store  *   *   */
end_comment

begin_class
specifier|public
class|class
name|IndexItem
implements|implements
name|Item
implements|,
name|StoreEntry
block|{
specifier|public
specifier|static
specifier|final
name|int
name|INDEX_SIZE
init|=
literal|51
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|INDEXES_ONLY_SIZE
init|=
literal|19
decl_stmt|;
specifier|protected
name|long
name|offset
init|=
name|POSITION_NOT_SET
decl_stmt|;
comment|// used by linked list
name|IndexItem
name|next
decl_stmt|;
name|IndexItem
name|prev
decl_stmt|;
specifier|private
name|long
name|previousItem
init|=
name|POSITION_NOT_SET
decl_stmt|;
specifier|private
name|long
name|nextItem
init|=
name|POSITION_NOT_SET
decl_stmt|;
specifier|private
name|boolean
name|active
init|=
literal|true
decl_stmt|;
comment|// TODO: consider just using a DataItem for the following fields.
specifier|private
name|long
name|keyOffset
init|=
name|POSITION_NOT_SET
decl_stmt|;
specifier|private
name|int
name|keyFile
init|=
operator|(
name|int
operator|)
name|POSITION_NOT_SET
decl_stmt|;
specifier|private
name|int
name|keySize
decl_stmt|;
specifier|private
name|long
name|valueOffset
init|=
name|POSITION_NOT_SET
decl_stmt|;
specifier|private
name|int
name|valueFile
init|=
operator|(
name|int
operator|)
name|POSITION_NOT_SET
decl_stmt|;
specifier|private
name|int
name|valueSize
decl_stmt|;
comment|/**      * Default Constructor      */
specifier|public
name|IndexItem
parameter_list|()
block|{     }
name|void
name|reset
parameter_list|()
block|{
name|previousItem
operator|=
name|POSITION_NOT_SET
expr_stmt|;
name|nextItem
operator|=
name|POSITION_NOT_SET
expr_stmt|;
name|keyOffset
operator|=
name|POSITION_NOT_SET
expr_stmt|;
name|keyFile
operator|=
operator|(
name|int
operator|)
name|POSITION_NOT_SET
expr_stmt|;
name|keySize
operator|=
literal|0
expr_stmt|;
name|valueOffset
operator|=
name|POSITION_NOT_SET
expr_stmt|;
name|valueFile
operator|=
operator|(
name|int
operator|)
name|POSITION_NOT_SET
expr_stmt|;
name|valueSize
operator|=
literal|0
expr_stmt|;
name|active
operator|=
literal|true
expr_stmt|;
block|}
comment|/**      * @return      * @see org.apache.activemq.kaha.StoreEntry#getKeyDataItem()      */
specifier|public
name|StoreLocation
name|getKeyDataItem
parameter_list|()
block|{
name|DataItem
name|result
init|=
operator|new
name|DataItem
argument_list|()
decl_stmt|;
name|result
operator|.
name|setOffset
argument_list|(
name|keyOffset
argument_list|)
expr_stmt|;
name|result
operator|.
name|setFile
argument_list|(
name|keyFile
argument_list|)
expr_stmt|;
name|result
operator|.
name|setSize
argument_list|(
name|keySize
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**      * @return      * @see org.apache.activemq.kaha.StoreEntry#getValueDataItem()      */
specifier|public
name|StoreLocation
name|getValueDataItem
parameter_list|()
block|{
name|DataItem
name|result
init|=
operator|new
name|DataItem
argument_list|()
decl_stmt|;
name|result
operator|.
name|setOffset
argument_list|(
name|valueOffset
argument_list|)
expr_stmt|;
name|result
operator|.
name|setFile
argument_list|(
name|valueFile
argument_list|)
expr_stmt|;
name|result
operator|.
name|setSize
argument_list|(
name|valueSize
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|public
name|void
name|setValueData
parameter_list|(
name|StoreLocation
name|item
parameter_list|)
block|{
name|valueOffset
operator|=
name|item
operator|.
name|getOffset
argument_list|()
expr_stmt|;
name|valueFile
operator|=
name|item
operator|.
name|getFile
argument_list|()
expr_stmt|;
name|valueSize
operator|=
name|item
operator|.
name|getSize
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|setKeyData
parameter_list|(
name|StoreLocation
name|item
parameter_list|)
block|{
name|keyOffset
operator|=
name|item
operator|.
name|getOffset
argument_list|()
expr_stmt|;
name|keyFile
operator|=
name|item
operator|.
name|getFile
argument_list|()
expr_stmt|;
name|keySize
operator|=
name|item
operator|.
name|getSize
argument_list|()
expr_stmt|;
block|}
comment|/**      * @param dataOut      * @throws IOException      */
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|dataOut
parameter_list|)
throws|throws
name|IOException
block|{
name|dataOut
operator|.
name|writeShort
argument_list|(
name|MAGIC
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|writeBoolean
argument_list|(
name|active
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|writeLong
argument_list|(
name|previousItem
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|writeLong
argument_list|(
name|nextItem
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|writeInt
argument_list|(
name|keyFile
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|writeLong
argument_list|(
name|keyOffset
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|writeInt
argument_list|(
name|keySize
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|writeInt
argument_list|(
name|valueFile
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|writeLong
argument_list|(
name|valueOffset
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|writeInt
argument_list|(
name|valueSize
argument_list|)
expr_stmt|;
block|}
name|void
name|updateIndexes
parameter_list|(
name|DataOutput
name|dataOut
parameter_list|)
throws|throws
name|IOException
block|{
name|dataOut
operator|.
name|writeShort
argument_list|(
name|MAGIC
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|writeBoolean
argument_list|(
name|active
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|writeLong
argument_list|(
name|previousItem
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|writeLong
argument_list|(
name|nextItem
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param dataIn      * @throws IOException      */
specifier|public
name|void
name|read
parameter_list|(
name|DataInput
name|dataIn
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|dataIn
operator|.
name|readShort
argument_list|()
operator|!=
name|MAGIC
condition|)
block|{
throw|throw
operator|new
name|BadMagicException
argument_list|()
throw|;
block|}
name|active
operator|=
name|dataIn
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|previousItem
operator|=
name|dataIn
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|nextItem
operator|=
name|dataIn
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|keyFile
operator|=
name|dataIn
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|keyOffset
operator|=
name|dataIn
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|keySize
operator|=
name|dataIn
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|valueFile
operator|=
name|dataIn
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|valueOffset
operator|=
name|dataIn
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|valueSize
operator|=
name|dataIn
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
name|void
name|readIndexes
parameter_list|(
name|DataInput
name|dataIn
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|dataIn
operator|.
name|readShort
argument_list|()
operator|!=
name|MAGIC
condition|)
block|{
throw|throw
operator|new
name|BadMagicException
argument_list|()
throw|;
block|}
name|active
operator|=
name|dataIn
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|previousItem
operator|=
name|dataIn
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|nextItem
operator|=
name|dataIn
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
comment|/**      * @param newPrevEntry      */
specifier|public
name|void
name|setPreviousItem
parameter_list|(
name|long
name|newPrevEntry
parameter_list|)
block|{
name|previousItem
operator|=
name|newPrevEntry
expr_stmt|;
block|}
comment|/**      * @return prev item      */
name|long
name|getPreviousItem
parameter_list|()
block|{
return|return
name|previousItem
return|;
block|}
comment|/**      * @param newNextEntry      */
specifier|public
name|void
name|setNextItem
parameter_list|(
name|long
name|newNextEntry
parameter_list|)
block|{
name|nextItem
operator|=
name|newNextEntry
expr_stmt|;
block|}
comment|/**      * @return      * @see org.apache.activemq.kaha.StoreEntry#getNextItem()      */
specifier|public
name|long
name|getNextItem
parameter_list|()
block|{
return|return
name|nextItem
return|;
block|}
comment|/**      * @param newObjectOffset      */
name|void
name|setKeyOffset
parameter_list|(
name|long
name|newObjectOffset
parameter_list|)
block|{
name|keyOffset
operator|=
name|newObjectOffset
expr_stmt|;
block|}
comment|/**      * @return key offset      */
name|long
name|getKeyOffset
parameter_list|()
block|{
return|return
name|keyOffset
return|;
block|}
comment|/**      * @return      * @see org.apache.activemq.kaha.StoreEntry#getKeyFile()      */
specifier|public
name|int
name|getKeyFile
parameter_list|()
block|{
return|return
name|keyFile
return|;
block|}
comment|/**      * @param keyFile The keyFile to set.      */
name|void
name|setKeyFile
parameter_list|(
name|int
name|keyFile
parameter_list|)
block|{
name|this
operator|.
name|keyFile
operator|=
name|keyFile
expr_stmt|;
block|}
comment|/**      * @return      * @see org.apache.activemq.kaha.StoreEntry#getValueFile()      */
specifier|public
name|int
name|getValueFile
parameter_list|()
block|{
return|return
name|valueFile
return|;
block|}
comment|/**      * @param valueFile The valueFile to set.      */
name|void
name|setValueFile
parameter_list|(
name|int
name|valueFile
parameter_list|)
block|{
name|this
operator|.
name|valueFile
operator|=
name|valueFile
expr_stmt|;
block|}
comment|/**      * @return      * @see org.apache.activemq.kaha.StoreEntry#getValueOffset()      */
specifier|public
name|long
name|getValueOffset
parameter_list|()
block|{
return|return
name|valueOffset
return|;
block|}
comment|/**      * @param valueOffset The valueOffset to set.      */
specifier|public
name|void
name|setValueOffset
parameter_list|(
name|long
name|valueOffset
parameter_list|)
block|{
name|this
operator|.
name|valueOffset
operator|=
name|valueOffset
expr_stmt|;
block|}
comment|/**      * @return Returns the active.      */
name|boolean
name|isActive
parameter_list|()
block|{
return|return
name|active
return|;
block|}
comment|/**      * @param active The active to set.      */
name|void
name|setActive
parameter_list|(
name|boolean
name|active
parameter_list|)
block|{
name|this
operator|.
name|active
operator|=
name|active
expr_stmt|;
block|}
comment|/**      * @return      * @see org.apache.activemq.kaha.StoreEntry#getOffset()      */
specifier|public
name|long
name|getOffset
parameter_list|()
block|{
return|return
name|offset
return|;
block|}
comment|/**      * @param offset The offset to set.      */
specifier|public
name|void
name|setOffset
parameter_list|(
name|long
name|offset
parameter_list|)
block|{
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
block|}
comment|/**      * @return      * @see org.apache.activemq.kaha.StoreEntry#getKeySize()      */
specifier|public
name|int
name|getKeySize
parameter_list|()
block|{
return|return
name|keySize
return|;
block|}
specifier|public
name|void
name|setKeySize
parameter_list|(
name|int
name|keySize
parameter_list|)
block|{
name|this
operator|.
name|keySize
operator|=
name|keySize
expr_stmt|;
block|}
comment|/**      * @return      * @see org.apache.activemq.kaha.StoreEntry#getValueSize()      */
specifier|public
name|int
name|getValueSize
parameter_list|()
block|{
return|return
name|valueSize
return|;
block|}
specifier|public
name|void
name|setValueSize
parameter_list|(
name|int
name|valueSize
parameter_list|)
block|{
name|this
operator|.
name|valueSize
operator|=
name|valueSize
expr_stmt|;
block|}
name|void
name|copyIndex
parameter_list|(
name|IndexItem
name|other
parameter_list|)
block|{
name|this
operator|.
name|offset
operator|=
name|other
operator|.
name|offset
expr_stmt|;
name|this
operator|.
name|active
operator|=
name|other
operator|.
name|active
expr_stmt|;
name|this
operator|.
name|previousItem
operator|=
name|other
operator|.
name|previousItem
expr_stmt|;
name|this
operator|.
name|nextItem
operator|=
name|other
operator|.
name|nextItem
expr_stmt|;
block|}
comment|/**      * @return print of 'this'      */
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|result
init|=
literal|"offset="
operator|+
name|offset
operator|+
literal|", key=("
operator|+
name|keyFile
operator|+
literal|", "
operator|+
name|keyOffset
operator|+
literal|", "
operator|+
name|keySize
operator|+
literal|")"
operator|+
literal|", value=("
operator|+
name|valueFile
operator|+
literal|", "
operator|+
name|valueOffset
operator|+
literal|", "
operator|+
name|valueSize
operator|+
literal|")"
operator|+
literal|", previousItem="
operator|+
name|previousItem
operator|+
literal|", nextItem="
operator|+
name|nextItem
decl_stmt|;
return|return
name|result
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
name|boolean
name|result
init|=
name|obj
operator|==
name|this
decl_stmt|;
if|if
condition|(
operator|!
name|result
operator|&&
name|obj
operator|!=
literal|null
operator|&&
name|obj
operator|instanceof
name|IndexItem
condition|)
block|{
name|IndexItem
name|other
init|=
operator|(
name|IndexItem
operator|)
name|obj
decl_stmt|;
name|result
operator|=
name|other
operator|.
name|offset
operator|==
name|this
operator|.
name|offset
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|offset
return|;
block|}
block|}
end_class

end_unit

