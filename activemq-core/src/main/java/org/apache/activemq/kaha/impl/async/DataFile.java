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
name|async
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|LinkedNode
import|;
end_import

begin_comment
comment|/**  * DataFile  *   * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
class|class
name|DataFile
extends|extends
name|LinkedNode
implements|implements
name|Comparable
block|{
specifier|private
specifier|final
name|File
name|file
decl_stmt|;
specifier|private
specifier|final
name|Integer
name|dataFileId
decl_stmt|;
specifier|private
specifier|final
name|int
name|preferedSize
decl_stmt|;
name|int
name|length
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|referenceCount
decl_stmt|;
name|DataFile
parameter_list|(
name|File
name|file
parameter_list|,
name|int
name|number
parameter_list|,
name|int
name|preferedSize
parameter_list|)
block|{
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
name|this
operator|.
name|preferedSize
operator|=
name|preferedSize
expr_stmt|;
name|this
operator|.
name|dataFileId
operator|=
operator|new
name|Integer
argument_list|(
name|number
argument_list|)
expr_stmt|;
name|length
operator|=
call|(
name|int
call|)
argument_list|(
name|file
operator|.
name|exists
argument_list|()
condition|?
name|file
operator|.
name|length
argument_list|()
else|:
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Integer
name|getDataFileId
parameter_list|()
block|{
return|return
name|dataFileId
return|;
block|}
specifier|public
specifier|synchronized
name|int
name|getLength
parameter_list|()
block|{
return|return
name|length
return|;
block|}
specifier|public
name|void
name|setLength
parameter_list|(
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|incrementLength
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|length
operator|+=
name|size
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|int
name|increment
parameter_list|()
block|{
return|return
operator|++
name|referenceCount
return|;
block|}
specifier|public
specifier|synchronized
name|int
name|decrement
parameter_list|()
block|{
return|return
operator|--
name|referenceCount
return|;
block|}
specifier|public
specifier|synchronized
name|boolean
name|isUnused
parameter_list|()
block|{
return|return
name|referenceCount
operator|<=
literal|0
return|;
block|}
specifier|public
specifier|synchronized
name|String
name|toString
parameter_list|()
block|{
name|String
name|result
init|=
name|file
operator|.
name|getName
argument_list|()
operator|+
literal|" number = "
operator|+
name|dataFileId
operator|+
literal|" , length = "
operator|+
name|length
operator|+
literal|" refCount = "
operator|+
name|referenceCount
decl_stmt|;
return|return
name|result
return|;
block|}
specifier|public
name|RandomAccessFile
name|openRandomAccessFile
parameter_list|(
name|boolean
name|appender
parameter_list|)
throws|throws
name|IOException
block|{
name|RandomAccessFile
name|rc
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|file
argument_list|,
literal|"rw"
argument_list|)
decl_stmt|;
comment|// When we start to write files size them up so that the OS has a chance
comment|// to allocate the file contigously.
if|if
condition|(
name|appender
condition|)
block|{
if|if
condition|(
name|length
operator|<
name|preferedSize
condition|)
block|{
name|rc
operator|.
name|setLength
argument_list|(
name|preferedSize
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|rc
return|;
block|}
specifier|public
name|void
name|closeRandomAccessFile
parameter_list|(
name|RandomAccessFile
name|file
parameter_list|)
throws|throws
name|IOException
block|{
comment|// On close set the file size to the real size.
if|if
condition|(
name|length
operator|!=
name|file
operator|.
name|length
argument_list|()
condition|)
block|{
name|file
operator|.
name|setLength
argument_list|(
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|boolean
name|delete
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|file
operator|.
name|delete
argument_list|()
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
name|DataFile
name|df
init|=
operator|(
name|DataFile
operator|)
name|o
decl_stmt|;
return|return
name|dataFileId
operator|-
name|df
operator|.
name|dataFileId
return|;
block|}
block|}
end_class

end_unit

