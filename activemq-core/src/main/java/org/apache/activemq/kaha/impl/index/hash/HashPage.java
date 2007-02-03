begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE  * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file  * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the  * License. You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  */
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
operator|.
name|hash
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
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Set
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

begin_comment
comment|/**  * A Page within a HashPage  *   * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
class|class
name|HashPage
block|{
specifier|static
specifier|final
name|int
name|PAGE_HEADER_SIZE
init|=
literal|17
decl_stmt|;
specifier|private
name|int
name|maximumEntries
decl_stmt|;
specifier|private
name|long
name|id
decl_stmt|;
specifier|private
name|int
name|binId
decl_stmt|;
specifier|private
name|int
name|persistedSize
init|=
literal|0
decl_stmt|;
specifier|private
name|List
argument_list|<
name|HashEntry
argument_list|>
name|hashIndexEntries
decl_stmt|;
comment|/*      * for persistence only      */
specifier|private
name|long
name|nextFreePageId
init|=
name|HashEntry
operator|.
name|NOT_SET
decl_stmt|;
specifier|private
name|boolean
name|active
init|=
literal|true
decl_stmt|;
comment|/**      * Constructor      *       * @param hashIndex      * @param id      * @param parentId      * @param maximumEntries      */
name|HashPage
parameter_list|(
name|long
name|id
parameter_list|,
name|int
name|maximumEntries
parameter_list|)
block|{
name|this
argument_list|(
name|maximumEntries
argument_list|)
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
comment|/**      * Constructor      *       * @param maximumEntries      */
specifier|public
name|HashPage
parameter_list|(
name|int
name|maximumEntries
parameter_list|)
block|{
name|this
operator|.
name|maximumEntries
operator|=
name|maximumEntries
expr_stmt|;
name|this
operator|.
name|hashIndexEntries
operator|=
operator|new
name|ArrayList
argument_list|<
name|HashEntry
argument_list|>
argument_list|(
name|maximumEntries
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"HashPage["
operator|+
name|getId
argument_list|()
operator|+
literal|":"
operator|+
name|binId
operator|+
literal|"]"
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|boolean
name|result
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|HashPage
condition|)
block|{
name|HashPage
name|other
init|=
operator|(
name|HashPage
operator|)
name|o
decl_stmt|;
name|result
operator|=
name|other
operator|.
name|id
operator|==
name|id
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
name|id
return|;
block|}
name|boolean
name|isActive
parameter_list|()
block|{
return|return
name|this
operator|.
name|active
return|;
block|}
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
name|long
name|getNextFreePageId
parameter_list|()
block|{
return|return
name|this
operator|.
name|nextFreePageId
return|;
block|}
name|void
name|setNextFreePageId
parameter_list|(
name|long
name|nextPageId
parameter_list|)
block|{
name|this
operator|.
name|nextFreePageId
operator|=
name|nextPageId
expr_stmt|;
block|}
name|long
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
name|void
name|setId
parameter_list|(
name|long
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
name|int
name|getPersistedSize
parameter_list|()
block|{
return|return
name|persistedSize
return|;
block|}
name|void
name|write
parameter_list|(
name|Marshaller
name|keyMarshaller
parameter_list|,
name|DataOutput
name|dataOut
parameter_list|)
throws|throws
name|IOException
block|{
name|writeHeader
argument_list|(
name|dataOut
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|writeInt
argument_list|(
name|hashIndexEntries
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|HashEntry
name|entry
range|:
name|hashIndexEntries
control|)
block|{
name|entry
operator|.
name|write
argument_list|(
name|keyMarshaller
argument_list|,
name|dataOut
argument_list|)
expr_stmt|;
block|}
block|}
name|void
name|read
parameter_list|(
name|Marshaller
name|keyMarshaller
parameter_list|,
name|DataInput
name|dataIn
parameter_list|)
throws|throws
name|IOException
block|{
name|readHeader
argument_list|(
name|dataIn
argument_list|)
expr_stmt|;
name|int
name|size
init|=
name|dataIn
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|hashIndexEntries
operator|.
name|clear
argument_list|()
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|HashEntry
name|entry
init|=
operator|new
name|HashEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|read
argument_list|(
name|keyMarshaller
argument_list|,
name|dataIn
argument_list|)
expr_stmt|;
name|hashIndexEntries
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
block|}
name|void
name|readHeader
parameter_list|(
name|DataInput
name|dataIn
parameter_list|)
throws|throws
name|IOException
block|{
name|active
operator|=
name|dataIn
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|nextFreePageId
operator|=
name|dataIn
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|binId
operator|=
name|dataIn
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|persistedSize
operator|=
name|dataIn
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
name|void
name|writeHeader
parameter_list|(
name|DataOutput
name|dataOut
parameter_list|)
throws|throws
name|IOException
block|{
name|dataOut
operator|.
name|writeBoolean
argument_list|(
name|isActive
argument_list|()
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|writeLong
argument_list|(
name|nextFreePageId
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|writeInt
argument_list|(
name|binId
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|writeInt
argument_list|(
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|hashIndexEntries
operator|.
name|isEmpty
argument_list|()
return|;
block|}
name|boolean
name|isFull
parameter_list|()
block|{
return|return
operator|(
name|hashIndexEntries
operator|.
name|size
argument_list|()
operator|>=
name|maximumEntries
operator|)
return|;
block|}
name|boolean
name|isUnderflowed
parameter_list|()
block|{
return|return
name|hashIndexEntries
operator|.
name|size
argument_list|()
operator|<
operator|(
name|maximumEntries
operator|/
literal|2
operator|)
return|;
block|}
name|boolean
name|isOverflowed
parameter_list|()
block|{
return|return
name|hashIndexEntries
operator|.
name|size
argument_list|()
operator|>
name|maximumEntries
return|;
block|}
name|List
argument_list|<
name|HashEntry
argument_list|>
name|getEntries
parameter_list|()
block|{
return|return
name|hashIndexEntries
return|;
block|}
name|void
name|setEntries
parameter_list|(
name|List
argument_list|<
name|HashEntry
argument_list|>
name|newEntries
parameter_list|)
block|{
name|this
operator|.
name|hashIndexEntries
operator|=
name|newEntries
expr_stmt|;
block|}
name|int
name|getMaximumEntries
parameter_list|()
block|{
return|return
name|this
operator|.
name|maximumEntries
return|;
block|}
name|void
name|setMaximumEntries
parameter_list|(
name|int
name|maximumEntries
parameter_list|)
block|{
name|this
operator|.
name|maximumEntries
operator|=
name|maximumEntries
expr_stmt|;
block|}
name|int
name|size
parameter_list|()
block|{
return|return
name|hashIndexEntries
operator|.
name|size
argument_list|()
return|;
block|}
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|hashIndexEntries
operator|.
name|clear
argument_list|()
expr_stmt|;
name|setNextFreePageId
argument_list|(
name|HashEntry
operator|.
name|NOT_SET
argument_list|)
expr_stmt|;
block|}
name|void
name|addHashEntry
parameter_list|(
name|int
name|index
parameter_list|,
name|HashEntry
name|entry
parameter_list|)
throws|throws
name|IOException
block|{
name|hashIndexEntries
operator|.
name|add
argument_list|(
name|index
argument_list|,
name|entry
argument_list|)
expr_stmt|;
block|}
name|HashEntry
name|getHashEntry
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|HashEntry
name|result
init|=
name|hashIndexEntries
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
return|return
name|result
return|;
block|}
name|HashEntry
name|removeHashEntry
parameter_list|(
name|int
name|index
parameter_list|)
throws|throws
name|IOException
block|{
name|HashEntry
name|result
init|=
name|hashIndexEntries
operator|.
name|remove
argument_list|(
name|index
argument_list|)
decl_stmt|;
return|return
name|result
return|;
block|}
name|void
name|removeAllTreeEntries
parameter_list|(
name|List
argument_list|<
name|HashEntry
argument_list|>
name|c
parameter_list|)
block|{
name|hashIndexEntries
operator|.
name|removeAll
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|HashEntry
argument_list|>
name|getSubList
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|)
block|{
return|return
operator|new
name|ArrayList
argument_list|<
name|HashEntry
argument_list|>
argument_list|(
name|hashIndexEntries
operator|.
name|subList
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * @return the binId      */
name|int
name|getBinId
parameter_list|()
block|{
return|return
name|this
operator|.
name|binId
return|;
block|}
comment|/**      * @param binId the binId to set      */
name|void
name|setBinId
parameter_list|(
name|int
name|binId
parameter_list|)
block|{
name|this
operator|.
name|binId
operator|=
name|binId
expr_stmt|;
block|}
name|void
name|dump
parameter_list|()
block|{
name|String
name|str
init|=
name|this
operator|+
literal|": "
decl_stmt|;
for|for
control|(
name|HashEntry
name|entry
range|:
name|hashIndexEntries
control|)
block|{
name|str
operator|+=
name|entry
operator|+
literal|","
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|str
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

