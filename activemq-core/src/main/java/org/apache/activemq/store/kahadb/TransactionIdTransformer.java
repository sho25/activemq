begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
operator|.
name|kahadb
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|TransactionId
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
name|store
operator|.
name|kahadb
operator|.
name|data
operator|.
name|KahaTransactionInfo
import|;
end_import

begin_interface
specifier|public
interface|interface
name|TransactionIdTransformer
block|{
name|KahaTransactionInfo
name|transform
parameter_list|(
name|TransactionId
name|txid
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

