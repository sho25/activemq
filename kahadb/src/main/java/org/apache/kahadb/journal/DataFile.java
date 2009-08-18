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
name|kahadb
operator|.
name|util
operator|.
name|IOHelper
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
name|LinkedNode
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
name|SequenceSet
import|;
end_import

begin_comment
comment|/**  * DataFile  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|DataFile
extends|extends
name|LinkedNode
argument_list|<
name|DataFile
argument_list|>
implements|implements
name|Comparable
argument_list|<
name|DataFile
argument_list|>
block|{
specifier|protected
specifier|final
name|File
name|file
decl_stmt|;
specifier|protected
specifier|final
name|Integer
name|dataFileId
decl_stmt|;
specifier|protected
name|int
name|length
decl_stmt|;
specifier|protected
specifier|final
name|SequenceSet
name|corruptedBlocks
init|=
operator|new
name|SequenceSet
argument_list|()
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
name|dataFileId
operator|=
name|Integer
operator|.
name|valueOf
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
name|File
name|getFile
parameter_list|()
block|{
return|return
name|file
return|;
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
name|String
name|toString
parameter_list|()
block|{
return|return
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
return|;
block|}
specifier|public
specifier|synchronized
name|RandomAccessFile
name|openRandomAccessFile
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|RandomAccessFile
argument_list|(
name|file
argument_list|,
literal|"rw"
argument_list|)
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|closeRandomAccessFile
parameter_list|(
name|RandomAccessFile
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
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
specifier|synchronized
name|void
name|move
parameter_list|(
name|File
name|targetDirectory
parameter_list|)
throws|throws
name|IOException
block|{
name|IOHelper
operator|.
name|moveFile
argument_list|(
name|file
argument_list|,
name|targetDirectory
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SequenceSet
name|getCorruptedBlocks
parameter_list|()
block|{
return|return
name|corruptedBlocks
return|;
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|DataFile
name|df
parameter_list|)
block|{
return|return
name|dataFileId
operator|-
name|df
operator|.
name|dataFileId
return|;
block|}
annotation|@
name|Override
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
name|DataFile
condition|)
block|{
name|result
operator|=
name|compareTo
argument_list|(
operator|(
name|DataFile
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
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|dataFileId
return|;
block|}
block|}
end_class

end_unit

