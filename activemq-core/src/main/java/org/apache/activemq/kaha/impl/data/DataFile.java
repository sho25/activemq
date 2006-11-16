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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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

begin_comment
comment|/**  * DataFile  *   * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
class|class
name|DataFile
block|{
specifier|private
name|File
name|file
decl_stmt|;
specifier|private
name|Integer
name|number
decl_stmt|;
specifier|private
name|int
name|referenceCount
decl_stmt|;
specifier|private
name|RandomAccessFile
name|randomAcessFile
decl_stmt|;
specifier|private
name|Object
name|writerData
decl_stmt|;
name|long
name|length
init|=
literal|0
decl_stmt|;
name|DataFile
parameter_list|(
name|File
name|file
parameter_list|,
name|int
name|number
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
name|number
operator|=
operator|new
name|Integer
argument_list|(
name|number
argument_list|)
expr_stmt|;
name|length
operator|=
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
expr_stmt|;
block|}
name|Integer
name|getNumber
parameter_list|()
block|{
return|return
name|number
return|;
block|}
specifier|synchronized
name|RandomAccessFile
name|getRandomAccessFile
parameter_list|()
throws|throws
name|FileNotFoundException
block|{
if|if
condition|(
name|randomAcessFile
operator|==
literal|null
condition|)
block|{
name|randomAcessFile
operator|=
operator|new
name|RandomAccessFile
argument_list|(
name|file
argument_list|,
literal|"rw"
argument_list|)
expr_stmt|;
block|}
return|return
name|randomAcessFile
return|;
block|}
specifier|synchronized
name|long
name|getLength
parameter_list|()
block|{
return|return
name|length
return|;
block|}
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
specifier|synchronized
name|void
name|purge
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|randomAcessFile
operator|!=
literal|null
condition|)
block|{
name|randomAcessFile
operator|.
name|close
argument_list|()
expr_stmt|;
name|randomAcessFile
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|synchronized
name|boolean
name|delete
parameter_list|()
throws|throws
name|IOException
block|{
name|purge
argument_list|()
expr_stmt|;
return|return
name|file
operator|.
name|delete
argument_list|()
return|;
block|}
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|randomAcessFile
operator|!=
literal|null
condition|)
block|{
name|randomAcessFile
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
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
name|number
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
comment|/**      * @return Opaque data that a DataFileWriter may want to associate with the DataFile.      */
specifier|public
specifier|synchronized
name|Object
name|getWriterData
parameter_list|()
block|{
return|return
name|writerData
return|;
block|}
comment|/** 	 * @param writerData - Opaque data that a DataFileWriter may want to associate with the DataFile. 	 */
specifier|public
specifier|synchronized
name|void
name|setWriterData
parameter_list|(
name|Object
name|writerData
parameter_list|)
block|{
name|this
operator|.
name|writerData
operator|=
name|writerData
expr_stmt|;
block|}
block|}
end_class

end_unit

