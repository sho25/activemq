begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
package|;
end_package

begin_interface
specifier|public
interface|interface
name|RedoListener
block|{
name|void
name|onRedoItem
parameter_list|(
name|DataItem
name|item
parameter_list|,
name|Object
name|object
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

