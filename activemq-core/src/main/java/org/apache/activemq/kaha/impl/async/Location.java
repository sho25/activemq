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
name|concurrent
operator|.
name|CountDownLatch
import|;
end_import

begin_comment
comment|/**  * Used as a location in the data store.  *   * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|Location
implements|implements
name|Comparable
argument_list|<
name|Location
argument_list|>
block|{
specifier|public
specifier|static
specifier|final
name|byte
name|MARK_TYPE
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|USER_TYPE
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|NOT_SET_TYPE
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|NOT_SET
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|int
name|dataFileId
init|=
name|NOT_SET
decl_stmt|;
specifier|private
name|int
name|offset
init|=
name|NOT_SET
decl_stmt|;
specifier|private
name|int
name|size
init|=
name|NOT_SET
decl_stmt|;
specifier|private
name|byte
name|type
init|=
name|NOT_SET_TYPE
decl_stmt|;
specifier|private
name|CountDownLatch
name|latch
decl_stmt|;
specifier|public
name|Location
parameter_list|()
block|{}
name|Location
parameter_list|(
name|Location
name|item
parameter_list|)
block|{
name|this
operator|.
name|dataFileId
operator|=
name|item
operator|.
name|dataFileId
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|item
operator|.
name|offset
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|item
operator|.
name|size
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|item
operator|.
name|type
expr_stmt|;
block|}
name|boolean
name|isValid
parameter_list|()
block|{
return|return
name|dataFileId
operator|!=
name|NOT_SET
return|;
block|}
comment|/**      * @return the size of the data record including the header.      */
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|/**      * @param size the size of the data record including the header.      */
specifier|public
name|void
name|setSize
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
comment|/**      * @return the size of the payload of the record.      */
specifier|public
name|int
name|getPaylodSize
parameter_list|()
block|{
return|return
name|size
operator|-
name|AsyncDataManager
operator|.
name|ITEM_HEAD_FOOT_SPACE
return|;
block|}
specifier|public
name|int
name|getOffset
parameter_list|()
block|{
return|return
name|offset
return|;
block|}
specifier|public
name|void
name|setOffset
parameter_list|(
name|int
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
specifier|public
name|int
name|getDataFileId
parameter_list|()
block|{
return|return
name|dataFileId
return|;
block|}
specifier|public
name|void
name|setDataFileId
parameter_list|(
name|int
name|file
parameter_list|)
block|{
name|this
operator|.
name|dataFileId
operator|=
name|file
expr_stmt|;
block|}
specifier|public
name|byte
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|public
name|void
name|setType
parameter_list|(
name|byte
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|result
init|=
literal|"offset = "
operator|+
name|offset
operator|+
literal|", file = "
operator|+
name|dataFileId
operator|+
literal|", size = "
operator|+
name|size
operator|+
literal|", type = "
operator|+
name|type
decl_stmt|;
return|return
name|result
return|;
block|}
specifier|public
name|void
name|writeExternal
parameter_list|(
name|DataOutput
name|dos
parameter_list|)
throws|throws
name|IOException
block|{
name|dos
operator|.
name|writeInt
argument_list|(
name|dataFileId
argument_list|)
expr_stmt|;
name|dos
operator|.
name|writeInt
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|dos
operator|.
name|writeInt
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|dos
operator|.
name|writeByte
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|readExternal
parameter_list|(
name|DataInput
name|dis
parameter_list|)
throws|throws
name|IOException
block|{
name|dataFileId
operator|=
name|dis
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|offset
operator|=
name|dis
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|size
operator|=
name|dis
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|type
operator|=
name|dis
operator|.
name|readByte
argument_list|()
expr_stmt|;
block|}
specifier|public
name|CountDownLatch
name|getLatch
parameter_list|()
block|{
return|return
name|latch
return|;
block|}
specifier|public
name|void
name|setLatch
parameter_list|(
name|CountDownLatch
name|latch
parameter_list|)
block|{
name|this
operator|.
name|latch
operator|=
name|latch
expr_stmt|;
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|Location
name|o
parameter_list|)
block|{
name|Location
name|l
init|=
operator|(
name|Location
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|dataFileId
operator|==
name|l
operator|.
name|dataFileId
condition|)
block|{
name|int
name|rc
init|=
name|offset
operator|-
name|l
operator|.
name|offset
decl_stmt|;
return|return
name|rc
return|;
block|}
return|return
name|dataFileId
operator|-
name|l
operator|.
name|dataFileId
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
name|Location
condition|)
block|{
name|result
operator|=
name|compareTo
argument_list|(
operator|(
name|Location
operator|)
name|o
argument_list|)
operator|==
literal|0
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
name|dataFileId
operator|^
name|offset
return|;
block|}
block|}
end_class

end_unit

