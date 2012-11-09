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
name|RedoListener
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
name|DataByteArrayInputStream
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
comment|/**  * Provides a Kaha DataManager Facade to the DataManager.  *   *   */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|DataManagerFacade
implements|implements
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
name|DataManager
block|{
specifier|private
specifier|static
specifier|final
name|ByteSequence
name|FORCE_COMMAND
init|=
operator|new
name|ByteSequence
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|'F'
block|,
literal|'O'
block|,
literal|'R'
block|,
literal|'C'
block|,
literal|'E'
block|}
argument_list|)
decl_stmt|;
specifier|private
name|AsyncDataManager
name|dataManager
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
name|Marshaller
name|redoMarshaller
decl_stmt|;
specifier|private
specifier|static
class|class
name|StoreLocationFacade
implements|implements
name|StoreLocation
block|{
specifier|private
specifier|final
name|Location
name|location
decl_stmt|;
specifier|public
name|StoreLocationFacade
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
name|int
name|getFile
parameter_list|()
block|{
return|return
name|location
operator|.
name|getDataFileId
argument_list|()
return|;
block|}
specifier|public
name|long
name|getOffset
parameter_list|()
block|{
return|return
name|location
operator|.
name|getOffset
argument_list|()
return|;
block|}
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
name|location
operator|.
name|getSize
argument_list|()
return|;
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
block|}
specifier|public
name|DataManagerFacade
parameter_list|(
name|AsyncDataManager
name|dataManager
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|dataManager
operator|=
name|dataManager
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|private
specifier|static
name|StoreLocation
name|convertToStoreLocation
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
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|StoreLocationFacade
argument_list|(
name|location
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Location
name|convertFromStoreLocation
parameter_list|(
name|StoreLocation
name|location
parameter_list|)
block|{
if|if
condition|(
name|location
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|location
operator|.
name|getClass
argument_list|()
operator|==
name|StoreLocationFacade
operator|.
name|class
condition|)
block|{
return|return
operator|(
operator|(
name|StoreLocationFacade
operator|)
name|location
operator|)
operator|.
name|getLocation
argument_list|()
return|;
block|}
name|Location
name|l
init|=
operator|new
name|Location
argument_list|()
decl_stmt|;
name|l
operator|.
name|setOffset
argument_list|(
operator|(
name|int
operator|)
name|location
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|l
operator|.
name|setSize
argument_list|(
name|location
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|l
operator|.
name|setDataFileId
argument_list|(
name|location
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|l
return|;
block|}
specifier|public
name|Object
name|readItem
parameter_list|(
name|Marshaller
name|marshaller
parameter_list|,
name|StoreLocation
name|location
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteSequence
name|sequence
init|=
name|dataManager
operator|.
name|read
argument_list|(
name|convertFromStoreLocation
argument_list|(
name|location
argument_list|)
argument_list|)
decl_stmt|;
name|DataByteArrayInputStream
name|dataIn
init|=
operator|new
name|DataByteArrayInputStream
argument_list|(
name|sequence
argument_list|)
decl_stmt|;
return|return
name|marshaller
operator|.
name|readPayload
argument_list|(
name|dataIn
argument_list|)
return|;
block|}
specifier|public
name|StoreLocation
name|storeDataItem
parameter_list|(
name|Marshaller
name|marshaller
parameter_list|,
name|Object
name|payload
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DataByteArrayOutputStream
name|buffer
init|=
operator|new
name|DataByteArrayOutputStream
argument_list|()
decl_stmt|;
name|marshaller
operator|.
name|writePayload
argument_list|(
name|payload
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
name|ByteSequence
name|data
init|=
name|buffer
operator|.
name|toByteSequence
argument_list|()
decl_stmt|;
return|return
name|convertToStoreLocation
argument_list|(
name|dataManager
operator|.
name|write
argument_list|(
name|data
argument_list|,
operator|(
name|byte
operator|)
literal|1
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|void
name|force
parameter_list|()
throws|throws
name|IOException
block|{
name|dataManager
operator|.
name|write
argument_list|(
name|FORCE_COMMAND
argument_list|,
operator|(
name|byte
operator|)
literal|2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|updateItem
parameter_list|(
name|StoreLocation
name|location
parameter_list|,
name|Marshaller
name|marshaller
parameter_list|,
name|Object
name|payload
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DataByteArrayOutputStream
name|buffer
init|=
operator|new
name|DataByteArrayOutputStream
argument_list|()
decl_stmt|;
name|marshaller
operator|.
name|writePayload
argument_list|(
name|payload
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
name|ByteSequence
name|data
init|=
name|buffer
operator|.
name|toByteSequence
argument_list|()
decl_stmt|;
name|dataManager
operator|.
name|update
argument_list|(
name|convertFromStoreLocation
argument_list|(
name|location
argument_list|)
argument_list|,
name|data
argument_list|,
literal|false
argument_list|)
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
name|void
name|consolidateDataFiles
parameter_list|()
throws|throws
name|IOException
block|{
name|dataManager
operator|.
name|consolidateDataFiles
argument_list|()
expr_stmt|;
block|}
specifier|public
name|boolean
name|delete
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|dataManager
operator|.
name|delete
argument_list|()
return|;
block|}
specifier|public
name|void
name|addInterestInFile
parameter_list|(
name|int
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|dataManager
operator|.
name|addInterestInFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeInterestInFile
parameter_list|(
name|int
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|dataManager
operator|.
name|removeInterestInFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|recoverRedoItems
parameter_list|(
name|RedoListener
name|listener
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Not Implemented.."
argument_list|)
throw|;
block|}
specifier|public
name|StoreLocation
name|storeRedoItem
parameter_list|(
name|Object
name|payload
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Not Implemented.."
argument_list|)
throw|;
block|}
specifier|public
name|Marshaller
name|getRedoMarshaller
parameter_list|()
block|{
return|return
name|redoMarshaller
return|;
block|}
specifier|public
name|void
name|setRedoMarshaller
parameter_list|(
name|Marshaller
name|redoMarshaller
parameter_list|)
block|{
name|this
operator|.
name|redoMarshaller
operator|=
name|redoMarshaller
expr_stmt|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
end_class

end_unit
