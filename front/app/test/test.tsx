import { getLegalAddresses } from '../lib/actions/location';
import DropdownSearch from '../ui/dropdown-search';

export default async function Test() {
  const data = await getLegalAddresses();

  return <DropdownSearch address={data} depth={'eupmyundong'} />;
}
